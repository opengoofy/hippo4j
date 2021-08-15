package com.github.dynamic.threadpool.starter.alarm;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BaseSendMessageService implements InitializingBean, SendMessageService {

    @NonNull
    private final List<AlarmConfig> alarmConfigs;

    private final List<SendMessageHandler> sendMessageHandlers = new ArrayList(4);

    @Override
    public void sendMessage(CustomThreadPoolExecutor threadPoolExecutor) {
        for (SendMessageHandler messageHandler : sendMessageHandlers) {
            try {
                messageHandler.sendMessage(alarmConfigs, threadPoolExecutor);
            } catch (Exception ex) {
                // ignore
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
