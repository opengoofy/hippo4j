package cn.hippo4j.starter.config;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.starter.alarm.AlarmControlHandler;
import cn.hippo4j.starter.alarm.BaseSendMessageService;
import cn.hippo4j.starter.alarm.SendMessageHandler;
import cn.hippo4j.starter.alarm.SendMessageService;
import cn.hippo4j.starter.alarm.ding.DingSendMessageHandler;
import cn.hippo4j.starter.alarm.lark.LarkSendMessageHandler;
import cn.hippo4j.starter.alarm.wechat.WeChatSendMessageHandler;
import cn.hippo4j.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
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
public class MessageAlarmConfiguration {

    private final BootstrapProperties properties;

    private final InstanceInfo instanceInfo;

    private ConfigurableEnvironment environment;

    public static final String ACTIVE_DEFAULT = "unknown";

    public static final String SEND_MESSAGE_BEAN_NAME = "hippo4JSendMessageService";

    @DependsOn("hippo4JApplicationContextHolder")
    @Bean(MessageAlarmConfiguration.SEND_MESSAGE_BEAN_NAME)
    public SendMessageService hippo4JSendMessageService(HttpAgent httpAgent, AlarmControlHandler alarmControlHandler) {
        return new BaseSendMessageService(httpAgent, properties, alarmControlHandler);
    }

    @Bean
    public SendMessageHandler dingSendMessageHandler() {
        String active = environment.getProperty("spring.profiles.active", ACTIVE_DEFAULT);
        return new DingSendMessageHandler(active, instanceInfo);
    }

    @Bean
    public SendMessageHandler larkSendMessageHandler() {
        String active = environment.getProperty("spring.profiles.active", ACTIVE_DEFAULT);
        return new LarkSendMessageHandler(active, instanceInfo);
    }

    @Bean
    public SendMessageHandler weChatSendMessageHandler() {
        String active = environment.getProperty("spring.profiles.active", ACTIVE_DEFAULT);
        return new WeChatSendMessageHandler(active, instanceInfo);
    }

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        return new AlarmControlHandler();
    }

}
