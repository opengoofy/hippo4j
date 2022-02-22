package cn.hippo4j.starter.core;

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.common.web.base.Results;
import cn.hippo4j.common.web.exception.ErrorCodeEnum;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

/**
 * Discovery client.
 *
 * @author chen.ma
 * @date 2021/7/13 21:51
 */
@Slf4j
public class DiscoveryClient implements DisposableBean {

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

        this.scheduler = new ScheduledThreadPoolExecutor(
                new Integer(1),
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.discovery.scheduler").build()
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

        String urlPath = BASE_PATH + "/apps/register/";
        Result registerResult;
        try {
            registerResult = httpAgent.httpPostByDiscovery(urlPath, instanceInfo);
        } catch (Exception ex) {
            registerResult = Results.failure(ErrorCodeEnum.SERVICE_ERROR);
            log.error("{}{} - registration failed :: {}", PREFIX, appPathIdentifier, ex.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info("{}{} - registration status :: {}", PREFIX, appPathIdentifier, registerResult.isSuccess() ? "success" : "fail");
        }

        return registerResult.isSuccess();
    }

    @Override
    public void destroy() throws Exception {
        log.info("{}{} - destroy service...", PREFIX, appPathIdentifier);
        String clientCloseUrlPath = Constants.BASE_PATH + "/client/close";
        Result clientCloseResult;
        try {
            String groupKeyIp = StrBuilder.create()
                    .append(instanceInfo.getGroupKey())
                    .append(Constants.GROUP_KEY_DELIMITER)
                    .append(instanceInfo.getIdentify())
                    .toString();

            ClientCloseHookExecute.ClientCloseHookReq clientCloseHookReq = new ClientCloseHookExecute.ClientCloseHookReq();
            clientCloseHookReq.setAppName(instanceInfo.getAppName())
                    .setInstanceId(instanceInfo.getInstanceId())
                    .setGroupKey(groupKeyIp);

            clientCloseResult = httpAgent.httpPostByDiscovery(clientCloseUrlPath, clientCloseHookReq);
            if (clientCloseResult.isSuccess()) {
                log.info("{}{} -client close hook success.", PREFIX, appPathIdentifier);
            }
        } catch (Throwable ex) {
            if (ex instanceof ShutdownExecuteException) {
                return;
            }

            log.error("{}{} - client close hook fail.", PREFIX, appPathIdentifier, ex);
        }
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

            renewResult = httpAgent.httpPostByDiscovery(BASE_PATH + "/apps/renew", instanceRenew);
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
