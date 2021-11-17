package cn.hippo4j.starter.alarm;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;

/**
 * Base send message service.
 *
 * @author chen.ma
 * @date 2021/8/15 15:34
 */
@Slf4j
@RequiredArgsConstructor
public class BaseSendMessageService implements InitializingBean, SendMessageService {

    @NonNull
    private final HttpAgent httpAgent;

    @NonNull
    private final BootstrapProperties properties;

    private final static Map<String, List<AlarmNotifyDTO>> ALARM_NOTIFY_CONFIG = Maps.newHashMap();


    private final Map<String, SendMessageHandler> sendMessageHandlers = Maps.newHashMap();

    @Override
    public void sendAlarmMessage(DynamicThreadPoolExecutor threadPoolExecutor) {
        List<AlarmNotifyDTO> notifyList = ALARM_NOTIFY_CONFIG.get(threadPoolExecutor.getThreadPoolId());
        if (CollUtil.isEmpty(notifyList)) {
            log.warn("Please configure alarm notification on the server.");
            return;
        }

        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("Please configure alarm notification on the server.");
                    return;
                }

                messageHandler.sendAlarmMessage(each, threadPoolExecutor);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool alarm notification.", ex);
            }
        });
    }

    @Override
    public void sendChangeMessage(PoolParameterInfo parameter) {
        List<AlarmNotifyDTO> notifyList = ALARM_NOTIFY_CONFIG.get(parameter.getTpId());
        if (CollUtil.isEmpty(notifyList)) {
            log.warn("Please configure alarm notification on the server.");
            return;
        }

        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("Please configure alarm notification on the server.");
                    return;
                }

                messageHandler.sendChangeMessage(each, parameter);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool change notification.", ex);
            }
        });
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, SendMessageHandler> sendMessageHandlerMap =
                ApplicationContextHolder.getBeansOfType(SendMessageHandler.class);
        sendMessageHandlerMap.values().forEach(each -> sendMessageHandlers.put(each.getType(), each));

        List<String> threadPoolIds = GlobalThreadPoolManage.listThreadPoolId();
        if (CollUtil.isEmpty(threadPoolIds)) {
            log.warn("The client does not have a dynamic thread pool instance configured.");
            return;
        }

        List<String> groupKeys = Lists.newArrayList();
        threadPoolIds.forEach(each -> {
            String groupKey = GroupKey.getKeyTenant(each, properties.getItemId(), properties.getNamespace());
            groupKeys.add(groupKey);
        });

        Result result = null;
        try {
            result = httpAgent.httpPostByDiscovery("/v1/cs/alarm/list/config", new ThreadPoolAlarmReqDTO(groupKeys));
        } catch (Throwable ex) {
            log.error("Get dynamic thread pool alarm configuration error.", ex);
            throw ex;
        }

        if (result.isSuccess() || result.getData() != null) {
            String resultDataStr = JSON.toJSONString(result.getData());
            List<ThreadPoolAlarmNotify> resultData = JSON.parseArray(resultDataStr, ThreadPoolAlarmNotify.class);
            resultData.forEach(each -> ALARM_NOTIFY_CONFIG.put(each.getThreadPoolId(), each.getAlarmNotifyList()));
        }
    }

    @Data
    @AllArgsConstructor
    static class ThreadPoolAlarmReqDTO {

        /**
         * groupKeys
         */
        private List<String> groupKeys;

    }

    @Data
    static class ThreadPoolAlarmNotify {

        /**
         * 线程池ID
         */
        private String threadPoolId;

        /**
         * 报警配置
         */
        private List<AlarmNotifyDTO> alarmNotifyList;

    }

}
