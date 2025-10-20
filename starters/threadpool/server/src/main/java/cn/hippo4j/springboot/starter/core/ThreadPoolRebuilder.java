/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-pool rebuild and switch helper.
 */
@Slf4j
public final class ThreadPoolRebuilder {

    private static final Map<String, ReentrantLock> REBUILD_LOCKS = new ConcurrentHashMap<>();

    private ThreadPoolRebuilder() {
    }

    public static boolean rebuildAndSwitch(ThreadPoolExecutor oldExecutor,
                                           Integer newQueueType,
                                           Integer capacity,
                                           String threadPoolId) {
        if (oldExecutor == null || newQueueType == null || threadPoolId == null) {
            return false;
        }
        ReentrantLock lock = REBUILD_LOCKS.computeIfAbsent(threadPoolId, k -> new ReentrantLock());
        if (!lock.tryLock()) {
            log.warn("Thread pool [{}] is already being rebuilt, skipping concurrent rebuild request.", threadPoolId);
            return false;
        }
        try {
            return doRebuildAndSwitch(oldExecutor, newQueueType, capacity, threadPoolId);
        } finally {
            try {
                lock.unlock();
            } finally {
                REBUILD_LOCKS.remove(threadPoolId, lock);
            }
        }
    }

    private static boolean doRebuildAndSwitch(ThreadPoolExecutor oldExecutor,
                                              Integer newQueueType,
                                              Integer capacity,
                                              String threadPoolId) {
        BlockingQueue<Runnable> newQueue = BlockingQueueTypeEnum.createBlockingQueue(newQueueType, capacity);
        if (newQueue == null) {
            log.warn("Rebuild skipped. Unable to create queue by type: {}", newQueueType);
            return false;
        }
        int core = oldExecutor.getCorePoolSize();
        int max = oldExecutor.getMaximumPoolSize();
        long keepAlive = oldExecutor.getKeepAliveTime(TimeUnit.SECONDS);
        ThreadFactory factory = oldExecutor.getThreadFactory();
        RejectedExecutionHandler rejected = oldExecutor.getRejectedExecutionHandler();
        boolean allowCoreTimeout = oldExecutor.allowsCoreThreadTimeOut();
        ThreadPoolExecutor newExecutor;
        if (oldExecutor instanceof DynamicThreadPoolExecutor) {
            DynamicThreadPoolExecutor dynOld = (DynamicThreadPoolExecutor) oldExecutor;
            Long executeTimeout = dynOld.getExecuteTimeOut();
            newExecutor = new DynamicThreadPoolExecutor(
                    core,
                    max,
                    keepAlive,
                    TimeUnit.SECONDS,
                    executeTimeout == null ? 0L : executeTimeout,
                    false,
                    0L,
                    newQueue,
                    threadPoolId,
                    factory,
                    rejected);
        } else {
            newExecutor = new ThreadPoolExecutor(core, max, keepAlive, TimeUnit.SECONDS, newQueue, factory, rejected);
        }
        newExecutor.allowCoreThreadTimeOut(allowCoreTimeout);
        try {
            newExecutor.prestartAllCoreThreads();
        } catch (Throwable ignore) {
        }
        boolean transferSuccess = false;
        try {
            transferQueuedTasks(oldExecutor, newExecutor);
            transferSuccess = true;
        } catch (Throwable ex) {
            log.error("Queue transfer failed for thread pool [{}], keeping old executor unchanged.", threadPoolId, ex);
        }
        if (!transferSuccess) {
            safeShutdownNow(newExecutor);
            return false;
        }
        ThreadPoolExecutorHolder oldHolder = switchRegistry(threadPoolId, newExecutor);
        oldExecutor.shutdown();
        awaitQuietly(oldExecutor, 500, TimeUnit.MILLISECONDS);
        if (!oldExecutor.isTerminated()) {
            log.warn("Old thread pool [{}] did not terminate within 500ms, {} tasks may still be running.",
                    threadPoolId, oldExecutor.getActiveCount());
        }
        return true;
    }

    private static void transferQueuedTasks(ThreadPoolExecutor from, ThreadPoolExecutor to) {
        BlockingQueue<Runnable> fromQueue = from.getQueue();
        int transferredCount = 0;
        int failedCount = 0;
        try {
            List<Runnable> batch = new ArrayList<>();
            fromQueue.drainTo(batch);
            for (Runnable r : batch) {
                try {
                    to.execute(r);
                    transferredCount++;
                } catch (Throwable ex) {
                    failedCount++;
                    try {
                        from.execute(r);
                        log.warn("Failed to transfer task to new executor, restored to old executor.", ex);
                    } catch (Throwable reEnqueueEx) {
                        if (!fromQueue.offer(r)) {
                            log.error("Failed to restore task to old executor, task may be lost.", reEnqueueEx);
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("Failed to drain tasks from old queue.", ex);
        }
        Runnable task;
        while ((task = fromQueue.poll()) != null) {
            try {
                to.execute(task);
                transferredCount++;
            } catch (Throwable ex) {
                failedCount++;
                try {
                    from.execute(task);
                    log.warn("Failed to transfer remaining task to new executor, restored to old executor.", ex);
                } catch (Throwable reEnqueueEx) {
                    if (!fromQueue.offer(task)) {
                        log.error("Failed to restore remaining task to old executor, task may be lost.", reEnqueueEx);
                    }
                }
            }
        }
        if (transferredCount > 0 || failedCount > 0) {
            log.info("Task transfer completed: {} tasks transferred, {} tasks failed (restored to old executor).", transferredCount, failedCount);
        }
        if (failedCount > 0) {
            throw new RuntimeException("Task transfer failed: " + failedCount + " tasks could not be transferred.");
        }
    }

    private static void safeShutdownNow(ThreadPoolExecutor executor) {
        try {
            List<Runnable> dropped = executor.shutdownNow();
            if (!dropped.isEmpty()) {
                log.warn("Dropped {} queued tasks during rollback.", dropped.size());
            }
        } catch (Throwable ignore) {
        }
    }

    private static void awaitQuietly(ThreadPoolExecutor executor, long time, TimeUnit unit) {
        try {
            executor.awaitTermination(time, unit);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        } catch (Throwable ignore) {
        }
    }

    private static ThreadPoolExecutorHolder switchRegistry(String threadPoolId, ThreadPoolExecutor newExecutor) {
        Map<String, ThreadPoolExecutorHolder> map = ThreadPoolExecutorRegistry.getHolderMap();
        ThreadPoolExecutorHolder holder = map.get(threadPoolId);
        ThreadPoolExecutorHolder newHolder = new ThreadPoolExecutorHolder(threadPoolId, newExecutor,
                holder == null ? null : holder.getExecutorProperties());
        ThreadPoolExecutorRegistry.putHolder(newHolder);
        return holder;
    }
}
