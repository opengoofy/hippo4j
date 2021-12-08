package cn.hippo4j.starter.core;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.common.web.exception.ErrorCodeEnum;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY;

/**
 * Discovery client.
 *
 * @author chen.ma
 * @date 2021/7/13 21:51
 */
@Slf4j
public class DiscoveryClient implements DisposableBean {

    private final ThreadPoolExecutor heartbeatExecutor;

    private final ScheduledExecutorService scheduler;

    private final HttpAgent httpAgent;

    private final InstanceInfo instanceInfo;

    private volatile long lastSuccessfulHeartbeatTimestamp = -1;

    private static final String PREFIX = "DiscoveryClient_";

    private final String appPathIdentifier;

    public DiscoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        this.httpAgent = httpAgent;
        this.instanceInfo = instanceInfo;
        this.appPathIdentifier = instanceInfo.getAppName().toUpperCase() + "/" + instanceInfo.getInstanceId();
        this.heartbeatExecutor = ThreadPoolBuilder.builder()
                .poolThreadSize(1, 5)
                .keepAliveTime(0, TimeUnit.SECONDS)
                .workQueue(new SynchronousQueue())
                .threadFactory("DiscoveryClient-HeartbeatExecutor", true)
                .build();

        this.scheduler = new ScheduledThreadPoolExecutor(
                new Integer(1),
                ThreadFactoryBuilder.builder().daemon(true).prefix("DiscoveryClient-Scheduler").build()
        );

        register();

        // init the schedule tasks
        initScheduledTasks();
    }

    private void initScheduledTasks() {
        scheduler.scheduleWithFixedDelay(new HeartbeatThread(), 30, 30, TimeUnit.SECONDS);
    }

    boolean register() {
        log.info("{}{} - registering service...", PREFIX, appPathIdentifier);

        String urlPath = Constants.BASE_PATH + "/apps/register/";
        Result registerResult;
        try {
            registerResult = httpAgent.httpPostByDiscovery(urlPath, instanceInfo);
        } catch (Exception ex) {
            registerResult = Results.failure(ErrorCodeEnum.SERVICE_ERROR);
            log.error("{}{} - registration failed :: {}", PREFIX, appPathIdentifier, ex.getMessage(), ex);
        }

        if (log.isInfoEnabled()) {
            log.info("{}{} - registration status :: {}", PREFIX, appPathIdentifier, registerResult.isSuccess() ? "success" : "fail");
        }

        return registerResult.isSuccess();
    }

    @Override
    public void destroy() throws Exception {
        log.info("{}{} - destroy service...", PREFIX, appPathIdentifier);
        String removeConfigCacheUrlPath = Constants.CONFIG_CONTROLLER_PATH + "/remove/config/cache";
        Result removeConfigCacheResult;
        try {
            String groupKeyIp = StrBuilder.create().append(instanceInfo.getGroupKey()).append(Constants.GROUP_KEY_DELIMITER).append(instanceInfo.getIdentify()).toString();
            Map<String, String> bodyMap = Maps.newHashMap();
            bodyMap.put(GROUP_KEY, groupKeyIp);
            removeConfigCacheResult = httpAgent.httpPostByDiscovery(removeConfigCacheUrlPath, bodyMap);
            if (removeConfigCacheResult.isSuccess()) {
                log.info("{}{} - remove config cache success.", PREFIX, appPathIdentifier);
            }
        } catch (Throwable ex) {
            log.error("{}{} - remove config cache fail.", PREFIX, appPathIdentifier, ex);
        }

        String removeNodeUrlPath = Constants.BASE_PATH + "/apps/remove/";
        Result removeNodeResult;
        try {
            removeNodeResult = httpAgent.httpPostByDiscovery(removeNodeUrlPath, instanceInfo);
            if (removeNodeResult.isSuccess()) {
                log.info("{}{} - destroy service success.", PREFIX, appPathIdentifier);
            }
        } catch (Throwable ex) {
            log.error("{}{} - destroy service fail.", PREFIX, appPathIdentifier, ex);
        }

        Optional.ofNullable(scheduler).ifPresent((each) -> each.shutdown());
    }

    public class HeartbeatThread implements Runnable {

        @Override
        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }
    }

    boolean renew() {
        Result renewResult;
        try {
            InstanceInfo.InstanceRenew instanceRenew = new InstanceInfo.InstanceRenew()
                    .setAppName(instanceInfo.getAppName())
                    .setInstanceId(instanceInfo.getInstanceId())
                    .setLastDirtyTimestamp(instanceInfo.getLastDirtyTimestamp().toString())
                    .setStatus(instanceInfo.getStatus().toString());

            renewResult = httpAgent.httpPostByDiscovery(Constants.BASE_PATH + "/apps/renew", instanceRenew);
            if (StrUtil.equals(ErrorCodeEnum.NOT_FOUND.getCode(), renewResult.getCode())) {
                long timestamp = instanceInfo.setIsDirtyWithTime();
                boolean success = register();
                if (success) {
                    instanceInfo.unsetIsDirty(timestamp);
                }
                return success;
            }

            return renewResult.isSuccess();
        } catch (Exception ex) {
            log.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, ex);
            return false;
        }
    }

}
