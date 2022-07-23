package cn.hippo4j.message.config;

import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.platform.DingSendMessageHandler;
import cn.hippo4j.message.platform.LarkSendMessageHandler;
import cn.hippo4j.message.platform.WeChatSendMessageHandler;
import cn.hippo4j.message.service.AlarmControlHandler;
import cn.hippo4j.message.service.HippoBaseSendMessageService;
import cn.hippo4j.message.service.HippoSendMessageService;
import cn.hippo4j.message.service.SendMessageHandler;
import org.springframework.context.annotation.Bean;

/**
 * Message configuration.
 */
public class MessageConfiguration {

    @Bean
    public HippoSendMessageService hippoSendMessageService(NotifyConfigBuilder serverNotifyConfigBuilder,
                                                           AlarmControlHandler alarmControlHandler) {
        return new HippoBaseSendMessageService(serverNotifyConfigBuilder, alarmControlHandler);
    }

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        return new AlarmControlHandler();
    }

    @Bean
    public SendMessageHandler dingSendMessageHandler() {
        return new DingSendMessageHandler();
    }

    @Bean
    public SendMessageHandler larkSendMessageHandler() {
        return new LarkSendMessageHandler();
    }

    @Bean
    public SendMessageHandler weChatSendMessageHandler() {
        return new WeChatSendMessageHandler();
    }
}
