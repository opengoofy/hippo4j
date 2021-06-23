package io.dynamic.threadpool.starter.listener;

import com.alibaba.fastjson.JSON;
import io.dynamic.threadpool.starter.common.Constants;
import io.dynamic.threadpool.starter.core.CacheData;
import io.dynamic.threadpool.starter.remote.HttpAgent;
import io.dynamict.hreadpool.common.model.GlobalRemotePoolInfo;
import io.dynamict.hreadpool.common.web.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端监听
 *
 * @author chen.ma
 * @date 2021/6/20 18:34
 */
@Slf4j
public class ClientWorker {

    private double currentLongingTaskCount = 0;

    private long timeout;

    private boolean isHealthServer = true;

    private final HttpAgent agent;

    private final ScheduledExecutorService executor;

    private final ScheduledExecutorService executorService;

    private final ConcurrentHashMap<String, CacheData> cacheMap = new ConcurrentHashMap(16);

    @SuppressWarnings("all")
    public ClientWorker(HttpAgent httpAgent) {
        this.agent = httpAgent;
        this.timeout = Constants.CONFIG_LONG_POLL_TIMEOUT;

        this.executor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setName("io.dynamic.threadPool.client.Worker.executor");
            t.setDaemon(true);
            return t;
        });

        int threadSize = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newScheduledThreadPool(threadSize, r -> {
            Thread t = new Thread(r);
            t.setName("io.dynamic.threadPool.client.Worker.longPolling.executor");
            t.setDaemon(true);
            return t;
        });

        this.executor.scheduleWithFixedDelay(() -> {
            try {
                checkConfigInfo();
            } catch (Throwable e) {
                log.error("[sub-check] rotate check error", e);
            }
        }, 1L, 10L, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查配置信息
     */
    public void checkConfigInfo() {
        int listenerSize = cacheMap.size();
        double perTaskConfigSize = 3000D;
        int longingTaskCount = (int) Math.ceil(listenerSize / perTaskConfigSize);

        if (longingTaskCount > currentLongingTaskCount) {
            for (int i = (int) currentLongingTaskCount; i < longingTaskCount; i++) {
                executorService.execute(new LongPollingRunnable(i));
            }
            currentLongingTaskCount = longingTaskCount;
        }
    }

    /**
     * 长轮训任务
     */
    class LongPollingRunnable implements Runnable {

        private final int taskId;

        public LongPollingRunnable(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            List<CacheData> cacheDataList = new ArrayList();

            List<String> changedTpIds = checkUpdateTpIds(cacheDataList);
            if (!CollectionUtils.isEmpty(cacheDataList)) {
                log.info("[dynamic threadPool] tpIds changed :: {}", changedTpIds);
            }

            for (String each : changedTpIds) {
                String[] keys = each.split(",");
                String namespace = keys[0];
                String itemId = keys[1];
                String tpId = keys[2];

                try {
                    String content = getServerConfig(namespace, itemId, tpId, 3000L);
                    CacheData cacheData = cacheMap.get(tpId);
                    cacheData.setContent(content);
                    cacheDataList.add(cacheData);
                    log.info("[data-received] namespace :: {}, itemId :: {}, tpId :: {}, md5 :: {}, content :: {}",
                            namespace, itemId, tpId, cacheData.getMd5(), content);
                } catch (Exception ex) {
                    // ignore
                }
            }

            for (CacheData each : cacheDataList) {
                each.checkListenerMd5();
            }
        }
    }

    /**
     * 检查修改的线程池 ID
     *
     * @param cacheDataList
     * @return
     */
    public List<String> checkUpdateTpIds(List<CacheData> cacheDataList) {
        Map<String, String> params = new HashMap(2);
        params.put(Constants.PROBE_MODIFY_REQUEST, JSON.toJSONString(cacheDataList));
        Map<String, String> headers = new HashMap(2);
        headers.put(Constants.LONG_PULLING_TIMEOUT, "" + timeout);

        if (StringUtils.isEmpty(cacheDataList)) {
            return Collections.emptyList();
        }

        try {
            long readTimeoutMs = timeout + (long) Math.round(timeout >> 1);
            Result result = agent.httpPost(Constants.LISTENER_PATH, headers, params, readTimeoutMs);
            if (result.isSuccess()) {
                setHealthServer(true);
                return parseUpdateDataIdResponse(result.getData().toString());
            } else {
                setHealthServer(false);
                log.error("[check-update] get changed dataId error, code: {}", result.getCode());
            }
        } catch (Exception ex) {
            setHealthServer(false);
            log.error("[check-update] get changed dataId exception.", ex);
        }

        return Collections.emptyList();
    }

    /**
     * 获取服务端配置
     *
     * @param namespace
     * @param itemId
     * @param tpId
     * @param readTimeout
     * @return
     */
    public String getServerConfig(String namespace, String itemId, String tpId, long readTimeout) {
        Map<String, String> params = new HashMap(3);
        params.put("namespace", namespace);
        params.put("itemId", itemId);
        params.put("tpId", tpId);

        Result result = agent.httpGet(Constants.CONFIG_CONTROLLER_PATH, null, params, readTimeout);
        if (result.isSuccess()) {
            return result.getData().toString();
        }

        log.error("[sub-server-error] namespace :: {}, itemId :: {}, tpId :: {}, result code :: {}",
                namespace, itemId, tpId, result.getCode());
        return Constants.NULL;
    }

    /**
     * Http 响应中获取变更的配置项
     *
     * @param response
     * @return
     */
    public List<String> parseUpdateDataIdResponse(String response) {
        return null;
    }

    /**
     * CacheData 添加 Listener
     *
     * @param namespace
     * @param itemId
     * @param tpId
     * @param listeners
     */
    public void addTenantListeners(String namespace, String itemId, String tpId, List<? extends Listener> listeners) {
        CacheData cacheData = addCacheDataIfAbsent(namespace, itemId, tpId);
        for (Listener listener : listeners) {
            cacheData.addListener(listener);
        }
    }

    /**
     * CacheData 不存在则添加
     *
     * @param namespace
     * @param itemId
     * @param tpId
     * @return
     */
    public CacheData addCacheDataIfAbsent(String namespace, String itemId, String tpId) {
        CacheData cacheData = cacheMap.get(tpId);
        if (cacheData != null) {
            return cacheData;
        }

        cacheData = new CacheData(tpId);
        CacheData lastCacheData = cacheMap.putIfAbsent(tpId, cacheData);
        if (lastCacheData == null) {
            String serverConfig = getServerConfig(namespace, itemId, tpId, 3000L);
            GlobalRemotePoolInfo poolInfo = JSON.parseObject(serverConfig, GlobalRemotePoolInfo.class);
            cacheData.setContent(poolInfo.getContent());

            int taskId = cacheMap.size() / Constants.CONFIG_LONG_POLL_TIMEOUT;
            cacheData.setTaskId(taskId);

            lastCacheData = cacheData;
        }

        return lastCacheData;
    }

    public boolean isHealthServer() {
        return this.isHealthServer;
    }

    private void setHealthServer(boolean isHealthServer) {
        this.isHealthServer = isHealthServer;
    }
}
