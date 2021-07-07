package io.dynamic.threadpool.starter.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.model.PoolParameterInfo;
import io.dynamic.threadpool.common.toolkit.ContentUtil;
import io.dynamic.threadpool.common.toolkit.GroupKey;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.starter.core.CacheData;
import io.dynamic.threadpool.starter.remote.HttpAgent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.dynamic.threadpool.common.constant.Constants.LINE_SEPARATOR;
import static io.dynamic.threadpool.common.constant.Constants.WORD_SEPARATOR;

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
                executorService.execute(new LongPollingRunnable());
            }
            currentLongingTaskCount = longingTaskCount;
        }
    }

    /**
     * 长轮训任务
     */
    class LongPollingRunnable implements Runnable {

        @SneakyThrows
        private void checkStatus() {
            // 服务端状态不正常睡眠 30s
            if (!isHealthServer) {
                log.error("[Check config] Error. exception message, Thread sleep 30 s.");
                Thread.sleep(30000);
            }
        }

        @Override
        @SneakyThrows
        public void run() {
            checkStatus();

            List<CacheData> cacheDataList = new ArrayList();
            List<CacheData> queryCacheDataList = cacheMap.entrySet()
                    .stream().map(each -> each.getValue()).collect(Collectors.toList());

            List<String> changedTpIds = checkUpdateDataIds(queryCacheDataList);
            if (!CollectionUtils.isEmpty(changedTpIds)) {
                log.info("[dynamic threadPool] tpIds changed :: {}", changedTpIds);
                for (String each : changedTpIds) {
                    String[] keys = StrUtil.split(each, Constants.GROUP_KEY_DELIMITER);
                    String tpId = keys[0];
                    String itemId = keys[1];
                    String namespace = keys[2];

                    try {
                        String content = getServerConfig(namespace, itemId, tpId, 3000L);
                        CacheData cacheData = cacheMap.get(tpId);
                        String poolContent = ContentUtil.getPoolContent(JSON.parseObject(content, PoolParameterInfo.class));
                        cacheData.setContent(poolContent);
                        cacheDataList.add(cacheData);
                        log.info("[data-received] namespace :: {}, itemId :: {}, tpId :: {}, md5 :: {}",
                                namespace, itemId, tpId, cacheData.getMd5());
                    } catch (Exception ex) {
                        // ignore
                    }
                }

                for (CacheData each : cacheDataList) {
                    each.checkListenerMd5();
                }
            }

            executorService.execute(this);
        }
    }

    /**
     * 转换入参
     *
     * @param cacheDataList
     * @return
     */
    private List<String> checkUpdateDataIds(List<CacheData> cacheDataList) {
        StringBuilder sb = new StringBuilder();
        for (CacheData cacheData : cacheDataList) {
            sb.append(cacheData.tpId).append(WORD_SEPARATOR);
            sb.append(cacheData.itemId).append(WORD_SEPARATOR);
            sb.append(cacheData.getMd5()).append(WORD_SEPARATOR);
            sb.append(cacheData.tenantId).append(LINE_SEPARATOR);
        }

        return checkUpdateTpIds(sb.toString());
    }

    /**
     * 检查修改的线程池 ID
     *
     * @param probeUpdateString
     * @return
     */
    public List<String> checkUpdateTpIds(String probeUpdateString) {
        Map<String, String> params = new HashMap(2);
        params.put(Constants.PROBE_MODIFY_REQUEST, probeUpdateString);
        Map<String, String> headers = new HashMap(2);
        headers.put(Constants.LONG_PULLING_TIMEOUT, "" + timeout);

        if (StringUtils.isEmpty(probeUpdateString)) {
            return Collections.emptyList();
        }

        try {
            long readTimeoutMs = timeout + (long) Math.round(timeout >> 1);
            Result result = agent.httpPost(Constants.LISTENER_PATH, headers, params, readTimeoutMs);
            if (result == null || result.isFail()) {
                log.warn("[check-update] get changed dataId error, code: {}", result == null ? "error" : result.getCode());
            } else {
                setHealthServer(true);
                return parseUpdateDataIdResponse(result.getData().toString());
            }
        } catch (Exception ex) {
            setHealthServer(false);
            log.error("[check-update] get changed dataId exception. error message :: {}", ex.getMessage());
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
        if (StringUtils.isEmpty(response)) {
            return Collections.emptyList();
        }

        try {
            response = URLDecoder.decode(response, "UTF-8");
        } catch (Exception e) {
            log.error("[polling-resp] decode modifiedDataIdsString error", e);
        }


        List<String> updateList = new LinkedList();
        for (String dataIdAndGroup : response.split(LINE_SEPARATOR)) {
            if (!StringUtils.isEmpty(dataIdAndGroup)) {
                String[] keyArr = dataIdAndGroup.split(WORD_SEPARATOR);
                String dataId = keyArr[0];
                String group = keyArr[1];
                if (keyArr.length == 2) {
                    updateList.add(GroupKey.getKey(dataId, group));
                    log.info("[{}] [polling-resp] config changed. dataId={}, group={}", dataId, group);
                } else if (keyArr.length == 3) {
                    String tenant = keyArr[2];
                    updateList.add(GroupKey.getKeyTenant(dataId, group, tenant));
                    log.info("[polling-resp] config changed. dataId={}, group={}, tenant={}", dataId, group, tenant);
                } else {
                    log.error("[{}] [polling-resp] invalid dataIdAndGroup error {}", dataIdAndGroup);
                }
            }
        }
        return updateList;
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

        cacheData = new CacheData(namespace, itemId, tpId);
        CacheData lastCacheData = cacheMap.putIfAbsent(tpId, cacheData);
        if (lastCacheData == null) {
            String serverConfig = null;
            try {
                serverConfig = getServerConfig(namespace, itemId, tpId, 3000L);
                PoolParameterInfo poolInfo = JSON.parseObject(serverConfig, PoolParameterInfo.class);
                cacheData.setContent(ContentUtil.getPoolContent(poolInfo));
            } catch (Exception ex) {
                log.error("[Cache Data] Error. Service Unavailable :: {}", ex.getMessage());
            }

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
