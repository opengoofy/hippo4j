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

package cn.hippo4j.config.service;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.MapUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.notify.NotifyCenter;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import cn.hippo4j.config.toolkit.ConfigExecutor;
import cn.hippo4j.config.toolkit.Md5ConfigUtil;
import cn.hippo4j.config.toolkit.RequestUtil;
import cn.hippo4j.server.common.base.Results;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.CLIENT_VERSION;
import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;

/**
 * Long polling service.
 */
@Slf4j
@Service
public class LongPollingService {

    private static final int FIXED_POLLING_INTERVAL_MS = 10000;

    private static final String TRUE_STR = "true";

    public static final String LONG_POLLING_HEADER = "Long-Pulling-Timeout";

    public static final String LONG_POLLING_NO_HANG_UP_HEADER = "Long-Pulling-Timeout-No-Hangup";

    public static final String CLIENT_APP_NAME_HEADER = "Client-AppName";

    private final Map<String, Long> retainIps = new ConcurrentHashMap<>();

    private static final long SCHEDULE_PERIOD = 30L;

    private static final int MAX_TIMEOUT = 10000;

    private static final int DEFAULT_DELAY_TIME = 500;

    public LongPollingService() {
        allSubs = new ConcurrentLinkedQueue<>();
        ConfigExecutor.scheduleLongPolling(new StatTask(), 0L, SCHEDULE_PERIOD, TimeUnit.SECONDS);
        NotifyCenter.registerToPublisher(LocalDataChangeEvent.class, NotifyCenter.RING_BUFFER_SIZE);
        NotifyCenter.registerSubscriber(new AbstractSubscriber() {

            @Override
            public void onEvent(AbstractEvent event) {
                if (!isFixedPolling() && event instanceof LocalDataChangeEvent) {
                    LocalDataChangeEvent evt = (LocalDataChangeEvent) event;
                    ConfigExecutor.executeLongPolling(new DataChangeTask(evt.getIdentify(), evt.getGroupKey()));
                }
            }

            @Override
            public Class<? extends AbstractEvent> subscribeType() {
                return LocalDataChangeEvent.class;
            }
        });
    }

    /**
     * Stat task.
     */
    class StatTask implements Runnable {

        @Override
        public void run() {
            log.info("Dynamic Thread Pool Long pulling client count: {}", allSubs.size());
        }
    }

    final Queue<ClientLongPolling> allSubs;

    /**
     * Data change task.
     */
    class DataChangeTask implements Runnable {

        final String identify;

        final String groupKey;

        DataChangeTask(String identify, String groupKey) {
            this.identify = identify;
            this.groupKey = groupKey;
        }

        @Override
        public void run() {
            try {
                for (Iterator<ClientLongPolling> iterator = allSubs.iterator(); iterator.hasNext();) {
                    ClientLongPolling clientSub = iterator.next();
                    String identity = groupKey + GROUP_KEY_DELIMITER + identify;
                    List<String> parseMapForFilter = CollectionUtil.newArrayList(identity);
                    if (StringUtil.isBlank(identify)) {
                        parseMapForFilter = MapUtil.parseMapForFilter(clientSub.clientMd5Map, groupKey);
                    }
                    parseMapForFilter.forEach(each -> {
                        if (clientSub.clientMd5Map.containsKey(each)) {
                            getRetainIps().put(clientSub.clientIdentify, System.currentTimeMillis());
                            ConfigCacheService.updateMd5(each, clientSub.clientIdentify, ConfigCacheService.getContentMd5(each));
                            iterator.remove();
                            clientSub.sendResponse(Collections.singletonList(groupKey));
                        }
                    });
                }
            } catch (Exception ex) {
                log.error("Data change error: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Add long polling client.
     *
     * @param req              http servlet request
     * @param rsp              http servlet response
     * @param clientMd5Map     client md5 map
     * @param probeRequestSize probe request size
     */
    public void addLongPollingClient(HttpServletRequest req, HttpServletResponse rsp, Map<String, String> clientMd5Map,
                                     int probeRequestSize) {
        String str = req.getHeader(LONG_POLLING_HEADER);
        String noHangUpFlag = req.getHeader(LONG_POLLING_NO_HANG_UP_HEADER);
        int delayTime = SwitchService.getSwitchInteger(SwitchService.FIXED_DELAY_TIME, DEFAULT_DELAY_TIME);
        long timeout = Math.max(MAX_TIMEOUT, Long.parseLong(str) - delayTime);
        boolean shouldReturn = false;

        if (isFixedPolling()) {
            timeout = Math.max(MAX_TIMEOUT, getFixedPollingInterval());
        } else {
            List<String> changedGroups = Md5ConfigUtil.compareMd5(req, clientMd5Map);
            if (!changedGroups.isEmpty()) {
                generateResponse(rsp, changedGroups);
                shouldReturn = true;
            } else if (noHangUpFlag != null && noHangUpFlag.equalsIgnoreCase(TRUE_STR)) {
                log.info("New initializing cacheData added in.");
                shouldReturn = true;
            }
        }

        if (!shouldReturn) {
            String clientIdentify = RequestUtil.getClientIdentify(req);
            final AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(0L);
            ConfigExecutor.executeLongPolling(new ClientLongPolling(asyncContext, clientMd5Map, clientIdentify, probeRequestSize,
                    timeout - delayTime, Pair.of(req.getHeader(CLIENT_APP_NAME_HEADER), req.getHeader(CLIENT_VERSION))));
        }
    }

    /**
     * Regularly check the configuration for changes.
     */
    class ClientLongPolling implements Runnable {

        final AsyncContext asyncContext;

        final Map<String, String> clientMd5Map;

        final long createTime;

        final String clientIdentify;

        final String appName;

        final String appVersion;

        final int probeRequestSize;

        final long timeoutTime;

        Future<?> asyncTimeoutFuture;

        ClientLongPolling(AsyncContext asyncContext, Map<String, String> clientMd5Map, String clientIdentify,
                          int probeRequestSize, long timeout, Pair<String, String> appInfo) {
            this.asyncContext = asyncContext;
            this.clientMd5Map = clientMd5Map;
            this.clientIdentify = clientIdentify;
            this.probeRequestSize = probeRequestSize;
            this.timeoutTime = timeout;
            this.appName = appInfo.getLeft();
            this.appVersion = appInfo.getRight();
            this.createTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            asyncTimeoutFuture = ConfigExecutor.scheduleLongPolling(() -> {
                try {
                    getRetainIps().put(ClientLongPolling.this.clientIdentify, System.currentTimeMillis());
                    allSubs.remove(ClientLongPolling.this);
                    if (isFixedPolling()) {
                        List<String> changedGroups = Md5ConfigUtil.compareMd5((HttpServletRequest) asyncContext.getRequest(), clientMd5Map);
                        if (!changedGroups.isEmpty()) {
                            sendResponse(changedGroups);
                        } else {
                            sendResponse(null);
                        }
                    } else {
                        sendResponse(null);
                    }
                } catch (Exception ex) {
                    log.error("Long polling error: {}", ex.getMessage(), ex);
                }
            }, timeoutTime, TimeUnit.MILLISECONDS);
            allSubs.add(this);
        }

        /**
         * Send response.
         *
         * @param changedGroups Changed thread pool group key
         */
        private void sendResponse(List<String> changedGroups) {
            // Cancel time out task.
            if (null != asyncTimeoutFuture) {
                asyncTimeoutFuture.cancel(false);
            }
            generateResponse(changedGroups);
        }

        /**
         * Generate async response.
         *
         * @param changedGroups changed thread pool group key
         */
        private void generateResponse(List<String> changedGroups) {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            if (CollectionUtil.isEmpty(changedGroups)) {
                if (StringUtil.isBlank(appVersion)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
                // Tell web container to send http response.
                asyncContext.complete();
                return;
            }
            try {
                String respStr = buildRespStr(changedGroups);
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(respStr);
            } catch (Exception ex) {
                log.error("Response client failed to return data.", ex);
            } finally {
                asyncContext.complete();
            }
        }
    }

    /**
     * Get retain ips.
     *
     * @return retain ips
     */
    public Map<String, Long> getRetainIps() {
        return retainIps;
    }

    /**
     * Generate sync response.
     *
     * @param response      response
     * @param changedGroups Changed thread pool group key
     */
    private void generateResponse(HttpServletResponse response, List<String> changedGroups) {
        if (CollectionUtil.isNotEmpty(changedGroups)) {
            try {
                String respStr = buildRespStr(changedGroups);
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(respStr);
            } catch (Exception ex) {
                log.error("Response client failed to return data.", ex);
            }
        }
    }

    /**
     * Build resp str.
     *
     * @param changedGroups Changed thread pool group key
     * @return resp str
     */
    @SneakyThrows
    private String buildRespStr(List<String> changedGroups) {
        String changedGroupStr = Md5Util.compareMd5ResultString(changedGroups);
        return JSONUtil.toJSONString(Results.success(changedGroupStr));
    }

    /**
     * Is support long polling.
     *
     * @param request http servlet request
     * @return is support long polling
     */
    public static boolean isSupportLongPolling(HttpServletRequest request) {
        return request.getHeader(LONG_POLLING_HEADER) != null;
    }

    /**
     * Is fixed polling.
     *
     * @return is fixed polling
     */
    private static boolean isFixedPolling() {
        return SwitchService.getSwitchBoolean(SwitchService.FIXED_POLLING, false);
    }

    /**
     * Get fixed polling interval.
     *
     * @return fixed polling interval
     */
    private static int getFixedPollingInterval() {
        return SwitchService.getSwitchInteger(SwitchService.FIXED_POLLING_INTERVAL, FIXED_POLLING_INTERVAL_MS);
    }
}
