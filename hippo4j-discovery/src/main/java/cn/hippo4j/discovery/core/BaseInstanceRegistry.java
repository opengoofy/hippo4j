package cn.hippo4j.discovery.core;

import cn.hippo4j.common.design.observer.AbstractSubjectCenter;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.model.InstanceInfo.InstanceStatus;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static cn.hippo4j.common.constant.Constants.EVICTION_INTERVAL_TIMER_IN_MS;
import static cn.hippo4j.common.constant.Constants.SCHEDULED_THREAD_CORE_NUM;

/**
 * Base instance registry.
 *
 * @author chen.ma
 * @date 2021/8/8 22:46
 */
@Slf4j
@Service
public class BaseInstanceRegistry implements InstanceRegistry<InstanceInfo> {

    private final int CONTAINER_SIZE = 1024;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock read = readWriteLock.readLock();

    protected final Object lock = new Object();

    private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry = new ConcurrentHashMap(CONTAINER_SIZE);

    protected volatile int expectedNumberOfClientsSendingRenews;

    private final CircularQueue<Pair<Long, String>> recentRegisteredQueue;

    private final CircularQueue<Pair<Long, String>> recentCanceledQueue;

    private ConcurrentLinkedQueue<RecentlyChangedItem> recentlyChangedQueue = new ConcurrentLinkedQueue();

    protected final ConcurrentMap<String, InstanceStatus> overriddenInstanceStatusMap = CacheBuilder
            .newBuilder().initialCapacity(512)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .<String, InstanceStatus>build().asMap();

    public BaseInstanceRegistry() {
        this.recentRegisteredQueue = new CircularQueue(CONTAINER_SIZE);
        this.recentCanceledQueue = new CircularQueue(CONTAINER_SIZE);
    }

    @Override
    public List<Lease<InstanceInfo>> listInstance(String appName) {
        Map<String, Lease<InstanceInfo>> appNameLeaseMap = registry.get(appName);
        if (CollectionUtils.isEmpty(appNameLeaseMap)) {
            return Lists.newArrayList();
        }

        List<Lease<InstanceInfo>> appNameLeaseList = Lists.newArrayList();
        appNameLeaseMap.values().forEach(each -> appNameLeaseList.add(each));
        return appNameLeaseList;
    }

    @Override
    public void register(InstanceInfo registrant) {
        read.lock();
        try {
            Map<String, Lease<InstanceInfo>> registerMap = registry.get(registrant.getAppName());
            if (registerMap == null) {
                ConcurrentHashMap<String, Lease<InstanceInfo>> registerNewMap = new ConcurrentHashMap(12);
                registerMap = registry.putIfAbsent(registrant.getAppName(), registerNewMap);
                if (registerMap == null) {
                    registerMap = registerNewMap;
                }
            }

            Lease<InstanceInfo> existingLease = registerMap.get(registrant.getInstanceId());
            if (existingLease != null && (existingLease.getHolder() != null)) {
                Long existingLastDirtyTimestamp = existingLease.getHolder().getLastDirtyTimestamp();
                Long registrationLastDirtyTimestamp = registrant.getLastDirtyTimestamp();

                if (existingLastDirtyTimestamp > registrationLastDirtyTimestamp) {
                    registrant = existingLease.getHolder();
                }
            }

            Lease<InstanceInfo> lease = new Lease(registrant);
            if (existingLease != null) {
                lease.setServiceUpTimestamp(existingLease.getServiceUpTimestamp());
            }
            registerMap.put(registrant.getInstanceId(), lease);

            recentRegisteredQueue.add(new Pair(
                    System.currentTimeMillis(),
                    registrant.getAppName() + "(" + registrant.getInstanceId() + ")"));

            InstanceStatus overriddenStatusFromMap = overriddenInstanceStatusMap.get(registrant.getInstanceId());
            if (overriddenStatusFromMap != null) {
                log.info("Storing overridden status :: {} from map", overriddenStatusFromMap);
                registrant.setOverriddenStatus(overriddenStatusFromMap);
            }

            if (InstanceStatus.UP.equals(registrant.getStatus())) {
                lease.serviceUp();
            }

            registrant.setActionType(InstanceInfo.ActionType.ADDED);
            recentlyChangedQueue.add(new RecentlyChangedItem(lease));
            registrant.setLastUpdatedTimestamp();
        } finally {
            read.unlock();
        }
    }

    @Override
    public boolean renew(InstanceInfo.InstanceRenew instanceRenew) {
        String appName = instanceRenew.getAppName();
        String instanceId = instanceRenew.getInstanceId();

        Map<String, Lease<InstanceInfo>> registryMap = registry.get(appName);
        Lease<InstanceInfo> leaseToRenew = null;
        if (registryMap == null || (leaseToRenew = registryMap.get(instanceId)) == null) {
            return false;
        }

        leaseToRenew.renew();
        return true;
    }

    @Override
    public void remove(InstanceInfo info) {
        ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            String appName = info.getAppName();
            String instanceId = info.getInstanceId();
            Map<String, Lease<InstanceInfo>> leaseMap = registry.get(appName);
            if (CollectionUtils.isEmpty(leaseMap)) {
                log.warn("Failed to remove unhealthy node, no application found :: {}", appName);
                return;
            }

            Lease<InstanceInfo> remove = leaseMap.remove(instanceId);
            if (remove == null) {
                log.warn("Failed to remove unhealthy node, no instance found :: {}", instanceId);
                return;
            }

            log.info("Remove unhealthy node, node ID :: {}", instanceId);
        } finally {
            writeLock.unlock();
        }
    }

    static class CircularQueue<E> extends AbstractQueue<E> {

        private final ArrayBlockingQueue<E> delegate;

        public CircularQueue(int capacity) {
            this.delegate = new ArrayBlockingQueue(capacity);
        }

        @Override
        public Iterator<E> iterator() {
            return delegate.iterator();
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean offer(E e) {
            while (!delegate.offer(e)) {
                delegate.poll();
            }
            return true;
        }

        @Override
        public E poll() {
            return delegate.poll();
        }

        @Override
        public E peek() {
            return delegate.peek();
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

    }

    private static final class RecentlyChangedItem {
        private long lastUpdateTime;

        private Lease<InstanceInfo> leaseInfo;

        public RecentlyChangedItem(Lease<InstanceInfo> lease) {
            this.leaseInfo = lease;
            lastUpdateTime = System.currentTimeMillis();
        }

        public long getLastUpdateTime() {
            return this.lastUpdateTime;
        }

        public Lease<InstanceInfo> getLeaseInfo() {
            return this.leaseInfo;
        }
    }

    public void evict(long additionalLeaseMs) {
        List<Lease<InstanceInfo>> expiredLeases = new ArrayList();
        for (Map.Entry<String, Map<String, Lease<InstanceInfo>>> groupEntry : registry.entrySet()) {
            Map<String, Lease<InstanceInfo>> leaseMap = groupEntry.getValue();
            if (leaseMap != null) {
                for (Map.Entry<String, Lease<InstanceInfo>> leaseEntry : leaseMap.entrySet()) {
                    Lease<InstanceInfo> lease = leaseEntry.getValue();
                    if (lease.isExpired(additionalLeaseMs) && lease.getHolder() != null) {
                        expiredLeases.add(lease);
                    }
                }
            }
        }

        for (Lease<InstanceInfo> expiredLease : expiredLeases) {
            String appName = expiredLease.getHolder().getAppName();
            String id = expiredLease.getHolder().getInstanceId();
            String identify = expiredLease.getHolder().getIdentify();
            internalCancel(appName, id, identify, false);
        }
    }

    protected boolean internalCancel(String appName, String id, String identify, boolean isReplication) {
        read.lock();
        try {
            Map<String, Lease<InstanceInfo>> registerMap = registry.get(appName);
            if (!CollectionUtils.isEmpty(registerMap)) {
                registerMap.remove(id);
                AbstractSubjectCenter.notify(AbstractSubjectCenter.SubjectType.CLEAR_CONFIG_CACHE, () -> identify);

                log.info("Clean up unhealthy nodes. Node id :: {}", id);
            }

        } finally {
            read.unlock();
        }

        return true;
    }

    public class EvictionTask extends TimerTask {

        private final AtomicLong lastExecutionNanosRef = new AtomicLong(0L);

        @Override
        public void run() {
            try {
                long compensationTimeMs = getCompensationTimeMs();
                log.info("Running the evict task with compensationTime {} ms", compensationTimeMs);
                evict(compensationTimeMs);
            } catch (Throwable e) {
                log.error("Could not run the evict task", e);
            }
        }

        long getCompensationTimeMs() {
            long currNanos = getCurrentTimeNano();
            long lastNanos = lastExecutionNanosRef.getAndSet(currNanos);
            if (lastNanos == 0L) {
                return 0L;
            }

            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(currNanos - lastNanos);
            long compensationTime = elapsedMs - EVICTION_INTERVAL_TIMER_IN_MS;
            return compensationTime <= 0L ? 0L : compensationTime;
        }

        long getCurrentTimeNano() {
            return System.nanoTime();
        }
    }

    private final ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(
                    SCHEDULED_THREAD_CORE_NUM,
                    new ThreadFactoryBuilder()
                            .setNameFormat("registry-eviction")
                            .setDaemon(true)
                            .build()
            );

    private final AtomicReference<EvictionTask> evictionTaskRef = new AtomicReference();

    public void postInit() {
        evictionTaskRef.set(new BaseInstanceRegistry.EvictionTask());
        scheduledExecutorService.scheduleWithFixedDelay(evictionTaskRef.get(),
                EVICTION_INTERVAL_TIMER_IN_MS, EVICTION_INTERVAL_TIMER_IN_MS, TimeUnit.MILLISECONDS);
    }

}
