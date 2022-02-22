package cn.hippo4j.starter.alarm;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

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

    @NonNull
    private final AlarmControlHandler alarmControlHandler;

    public final static Map<String, List<NotifyDTO>> ALARM_NOTIFY_CONFIG = Maps.newHashMap();

    private final Map<String, SendMessageHandler> sendMessageHandlers = Maps.newHashMap();

    @Override
    public void sendAlarmMessage(MessageTypeEnum typeEnum, DynamicThreadPoolExecutor executor) {
        String threadPoolId = executor.getThreadPoolId();
        String buildKey = StrUtil.builder(executor.getThreadPoolId(), "+", "ALARM").toString();
        List<NotifyDTO> notifyList = ALARM_NOTIFY_CONFIG.get(buildKey);
        if (CollUtil.isEmpty(notifyList)) {
            log.warn("Please configure alarm notification on the server. key :: [{}]", threadPoolId);
            return;
        }

        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("Please configure alarm notification on the server. key :: [{}]", threadPoolId);
                    return;
                }

                if (isSendAlarm(each.getTpId(), each.setTypeEnum(typeEnum))) {
                    messageHandler.sendAlarmMessage(each, executor);
                }
            } catch (Exception ex) {
                log.warn("Failed to send thread pool alarm notification. key :: [{}]", threadPoolId, ex);
            }
        });
    }

    @Override
    public void sendChangeMessage(PoolParameterInfo parameter) {
        String threadPoolId = parameter.getTpId();
        String buildKey = StrUtil.builder(parameter.getTpId(), "+", "CONFIG").toString();
        List<NotifyDTO> notifyList = ALARM_NOTIFY_CONFIG.get(buildKey);
        if (CollUtil.isEmpty(notifyList)) {
            log.warn("Please configure alarm notification on the server. key :: [{}]", threadPoolId);
            return;
        }

        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("Please configure alarm notification on the server. key :: [{}]", threadPoolId);
                    return;
                }

                messageHandler.sendChangeMessage(each, parameter);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool change notification. key :: [{}]", threadPoolId, ex);
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
            result = httpAgent.httpPostByDiscovery(BASE_PATH + "/notify/list/config", new ThreadPoolNotifyReqDTO(groupKeys));
        } catch (Throwable ex) {
            log.error("Get dynamic thread pool notify configuration error. message :: {}", ex.getMessage());
        }

        if (result != null && result.isSuccess() && result.getData() != null) {
            String resultDataStr = JSONUtil.toJSONString(result.getData());
            List<ThreadPoolNotify> resultData = JSONUtil.parseArray(resultDataStr, ThreadPoolNotify.class);
            resultData.forEach(each -> ALARM_NOTIFY_CONFIG.put(each.getNotifyKey(), each.getNotifyList()));

            ALARM_NOTIFY_CONFIG.forEach((key, val) ->
                    val.stream().filter(each -> StrUtil.equals("ALARM", each.getType()))
                            .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval()))
            );
        }
    }

    private boolean isSendAlarm(String threadPoolId, NotifyDTO notifyInfo) {
        AlarmControlDTO alarmControl = AlarmControlDTO.builder()
                .threadPool(threadPoolId)
                .platform(notifyInfo.getPlatform())
                .typeEnum(notifyInfo.getTypeEnum())
                .build();

        return alarmControlHandler.isSendAlarm(alarmControl);
    }

    @Data
    @AllArgsConstructor
    static class ThreadPoolNotifyReqDTO {

        /**
         * groupKeys
         */
        private List<String> groupKeys;

    }

    @Data
    static class ThreadPoolNotify {

        /**
         * 通知 Key
         */
        private String notifyKey;

        /**
         * 报警配置
         */
        private List<NotifyDTO> notifyList;

    }

}
