package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base Send Message Service.
 *
 * @author chen.ma
 * @date 2021/8/15 15:34
 */
@Slf4j
@RequiredArgsConstructor
public class BaseSendMessageService implements InitializingBean, SendMessageService {

    @NonNull
    private final List<AlarmConfig> alarmConfigs;

    private final List<SendMessageHandler> sendMessageHandlers = new ArrayList(4);

    @Override
    public void sendAlarmMessage(CustomThreadPoolExecutor threadPoolExecutor) {
        for (SendMessageHandler messageHandler : sendMessageHandlers) {
            try {
                messageHandler.sendAlarmMessage(alarmConfigs, threadPoolExecutor);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool alarm notification.", ex);
            }
        }
    }

    @Override
    public void sendChangeMessage(PoolParameterInfo parameter) {
        for (SendMessageHandler messageHandler : sendMessageHandlers) {
            try {
                messageHandler.sendChangeMessage(alarmConfigs, parameter);
            } catch (Exception ex) {
                log.warn("Failed to send thread pool change notification.", ex);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, SendMessageHandler> sendMessageHandlerMap =
                ApplicationContextHolder.getBeansOfType(SendMessageHandler.class);
        sendMessageHandlerMap.values().forEach(each -> sendMessageHandlers.add(each));
    }

}
