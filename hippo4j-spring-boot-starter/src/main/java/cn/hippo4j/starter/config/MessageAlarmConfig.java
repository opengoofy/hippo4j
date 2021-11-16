package cn.hippo4j.starter.config;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.starter.alarm.*;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Optional;

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
        Long alarmInterval = Optional.ofNullable(properties.getAlarmInterval()).orElse(5L);
        return new DingSendMessageHandler(active, alarmInterval, instanceInfo);
    }

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        Long alarmInterval = properties.getAlarmInterval();
        return new AlarmControlHandler(alarmInterval);
    }

}
