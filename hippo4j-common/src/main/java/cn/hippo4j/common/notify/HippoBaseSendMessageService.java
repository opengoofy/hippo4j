package cn.hippo4j.common.notify;

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.notify.request.AlarmNotifyRequest;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Map;

/**
 * Base send message service impl.
 *
 * @author chen.ma
 * @date 2022/2/22 21:32
 */
@Slf4j
@RequiredArgsConstructor
public class HippoBaseSendMessageService implements HippoSendMessageService, CommandLineRunner {

    private final NotifyConfigBuilder notifyConfigBuilder;

    private final AlarmControlHandler alarmControlHandler;

    private final Map<String, List<NotifyConfigDTO>> notifyConfigs = Maps.newHashMap();

    private final Map<String, SendMessageHandler> sendMessageHandlers = Maps.newHashMap();

    @Override
    public void sendAlarmMessage(NotifyTypeEnum typeEnum, AlarmNotifyRequest alarmNotifyRequest) {
        String threadPoolId = alarmNotifyRequest.getThreadPoolId();
        String buildKey = StrUtil.builder(threadPoolId, "+", "ALARM").toString();
        List<NotifyConfigDTO> notifyList = notifyConfigs.get(buildKey);
        if (CollUtil.isEmpty(notifyList)) {
            return;
        }

        notifyList.forEach(each -> {
            try {
                SendMessageHandler messageHandler = sendMessageHandlers.get(each.getPlatform());
                if (messageHandler == null) {
                    log.warn("Please configure alarm notification on the server. key :: [{}]", threadPoolId);
                    return;
                }

                if (isSendAlarm(each.getTpId(), each.getPlatform(), typeEnum)) {
                    alarmNotifyRequest.setNotifyTypeEnum(typeEnum);
                    messageHandler.sendAlarmMessage(each, alarmNotifyRequest);
                }
            } catch (Exception ex) {
                log.warn("Failed to send thread pool alarm notification. key :: [{}]", threadPoolId, ex);
            }
        });
    }

    @Override
    public void sendChangeMessage(ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();
        String buildKey = StrUtil.builder(threadPoolId, "+", "CONFIG").toString();
        List<NotifyConfigDTO> notifyList = notifyConfigs.get(buildKey);
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

                messageHandler.sendChangeMessage(each, changeParameterNotifyRequest);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool change notification. key :: [{}]", threadPoolId, ex);
            }
        });
    }

    /**
     * Is send alarm.
     *
     * @param threadPoolId
     * @param platform
     * @param typeEnum
     * @return
     */
    private boolean isSendAlarm(String threadPoolId, String platform, NotifyTypeEnum typeEnum) {
        AlarmControlDTO alarmControl = AlarmControlDTO.builder()
                .threadPool(threadPoolId)
                .platform(platform)
                .typeEnum(typeEnum)
                .build();

        return alarmControlHandler.isSendAlarm(alarmControl);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, SendMessageHandler> sendMessageHandlerMap =
                ApplicationContextHolder.getBeansOfType(SendMessageHandler.class);
        sendMessageHandlerMap.values().forEach(each -> sendMessageHandlers.put(each.getType(), each));

        Map<String, List<NotifyConfigDTO>> buildNotify = notifyConfigBuilder.buildNotify();
        notifyConfigs.putAll(buildNotify);
    }

    /**
     * Put platform.
     *
     * @param notifyConfigs
     */
    public synchronized void putPlatform(Map<String, List<NotifyConfigDTO>> notifyConfigs) {
        this.notifyConfigs.putAll(notifyConfigs);
    }

}
