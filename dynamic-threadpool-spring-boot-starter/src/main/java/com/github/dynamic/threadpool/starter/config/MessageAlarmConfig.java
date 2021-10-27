package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.starter.alarm.BaseSendMessageService;
import com.github.dynamic.threadpool.starter.alarm.DingSendMessageHandler;
import com.github.dynamic.threadpool.starter.alarm.SendMessageHandler;
import com.github.dynamic.threadpool.starter.alarm.SendMessageService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Message alarm config.
 *
 * @author chen.ma
 * @date 2021/8/15 15:39
 */
@AllArgsConstructor
public class MessageAlarmConfig {

    private final BootstrapProperties properties;

    private final InstanceInfo instanceInfo;

    private ConfigurableEnvironment environment;

    public static final String SEND_MESSAGE_BEAN_NAME = "sendMessageService";

    @DependsOn("applicationContextHolder")
    @Bean(MessageAlarmConfig.SEND_MESSAGE_BEAN_NAME)
    public SendMessageService sendMessageService() {
        return new BaseSendMessageService(properties.getNotifys());
    }

    @Bean
    public SendMessageHandler dingSendMessageHandler() {
        String active = environment.getProperty("spring.profiles.active", Strings.EMPTY);
        return new DingSendMessageHandler(active, instanceInfo);
    }

}
