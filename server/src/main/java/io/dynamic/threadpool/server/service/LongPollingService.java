package io.dynamic.threadpool.server.service;

import io.dynamic.threadpool.server.event.LocalDataChangeEvent;
import io.dynamic.threadpool.server.notify.Event;
import io.dynamic.threadpool.server.notify.NotifyCenter;
import io.dynamic.threadpool.server.notify.listener.Subscriber;
import io.dynamic.threadpool.server.toolkit.ConfigExecutor;
import io.dynamic.threadpool.server.toolkit.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 长轮询服务
 *
 * @author chen.ma
 * @date 2021/6/22 23:14
 */
@Slf4j
@Service
public class LongPollingService {

    private static final int FIXED_POLLING_INTERVAL_MS = 10000;

    public static final String LONG_POLLING_HEADER = "Long-Pulling-Timeout";

    public static final String LONG_POLLING_NO_HANG_UP_HEADER = "Long-Pulling-Timeout-No-Hangup";

    public static final String CLIENT_APPNAME_HEADER = "Client-AppName";

    private Map<String, Long> retainIps = new ConcurrentHashMap();

    public LongPollingService() {
        allSubs = new ConcurrentLinkedQueue();

        ConfigExecutor.scheduleLongPolling(new StatTask(), 0L, 10L, TimeUnit.SECONDS);

        NotifyCenter.registerSubscriber(new Subscriber() {

            @Override
            public void onEvent(Event event) {
                if (isFixedPolling()) {
                    // Ignore.
                } else {
                    if (event instanceof LocalDataChangeEvent) {
                        LocalDataChangeEvent evt = (LocalDataChangeEvent) event;
                        // ConfigExecutor.executeLongPolling(new DataChangeTask(evt.groupKey, evt.isBeta, evt.betaIps));
                    }
                }
            }

            @Override
            public Class<? extends Event> subscribeType() {
                return LocalDataChangeEvent.class;
            }
        });
    }

    class StatTask implements Runnable {

        @Override
        public void run() {
            log.info("[long-pulling] client count " + allSubs.size());
        }
    }

    class DataChangeTask implements Runnable {

        final String groupKey;

        final long changeTime = System.currentTimeMillis();

        final boolean isBeta;

        final List<String> betaIps;

        DataChangeTask(String groupKey, boolean isBeta, List<String> betaIps) {
            this.groupKey = groupKey;
            this.isBeta = isBeta;
            this.betaIps = betaIps;
        }

        @Override
        public void run() {

        }
    }


    public static boolean isSupportLongPolling(HttpServletRequest req) {
        return null != req.getHeader(LONG_POLLING_HEADER);
    }

    private static boolean isFixedPolling() {
        return SwitchService.getSwitchBoolean(SwitchService.FIXED_POLLING, false);
    }

    private static int getFixedPollingInterval() {
        return SwitchService.getSwitchInteger(SwitchService.FIXED_POLLING_INTERVAL, FIXED_POLLING_INTERVAL_MS);
    }

    public void addLongPollingClient(HttpServletRequest req, HttpServletResponse rsp, Map<String, String> clientMd5Map,
                                     int probeRequestSize) {
        String str = req.getHeader(LONG_POLLING_HEADER);
        String noHangUpFlag = req.getHeader(LONG_POLLING_NO_HANG_UP_HEADER);
        String appName = req.getHeader(CLIENT_APPNAME_HEADER);
        int delayTime = SwitchService.getSwitchInteger(SwitchService.FIXED_DELAY_TIME, 500);

        long timeout = Math.max(10000, Long.parseLong(str) - delayTime);
        if (isFixedPolling()) {
            timeout = Math.max(10000, getFixedPollingInterval());
        } else {

        }

        String ip = RequestUtil.getRemoteIp(req);

        final AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0L);

        ConfigExecutor.executeLongPolling(new ClientLongPolling(asyncContext, clientMd5Map, ip, probeRequestSize, timeout, appName));
    }

    final Queue<ClientLongPolling> allSubs;

    class ClientLongPolling implements Runnable {

        final AsyncContext asyncContext;

        final Map<String, String> clientMd5Map;

        final long createTime;

        final String ip;

        final String appName;

        final int probeRequestSize;

        final long timeoutTime;

        Future<?> asyncTimeoutFuture;

        public ClientLongPolling(AsyncContext asyncContext, Map<String, String> clientMd5Map, String ip, int probeRequestSize, long timeout, String appName) {
            this.asyncContext = asyncContext;
            this.clientMd5Map = clientMd5Map;
            this.ip = ip;
            this.probeRequestSize = probeRequestSize;
            this.timeoutTime = timeout;
            this.appName = appName;
            this.createTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            asyncTimeoutFuture = ConfigExecutor.scheduleLongPolling(() -> {
                getRetainIps().put(ClientLongPolling.this.ip, System.currentTimeMillis());
                allSubs.remove(ClientLongPolling.this);

                if (isFixedPolling()) {

                }

            }, timeoutTime, TimeUnit.MILLISECONDS);

            allSubs.add(this);
        }

    }

    public Map<String, Long> getRetainIps() {
        return retainIps;
    }
}
