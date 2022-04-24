package cn.hippo4j.starter.config;

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.notify.*;
import cn.hippo4j.common.notify.platform.DingSendMessageHandler;
import cn.hippo4j.common.notify.platform.LarkSendMessageHandler;
import cn.hippo4j.common.notify.platform.WeChatSendMessageHandler;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.starter.core.ServerThreadPoolDynamicRefresh;
import cn.hippo4j.starter.notify.ServerNotifyConfigBuilder;
import cn.hippo4j.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;

/**
 * Message notify config.
 *
 * @author chen.ma
 * @date 2021/8/15 15:39
 */
@AllArgsConstructor
public class MessageNotifyConfiguration {

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        return new AlarmControlHandler();
    }

    @Bean
    public NotifyConfigBuilder notifyConfigBuilder(HttpAgent httpAgent,
                                                   BootstrapProperties properties,
                                                   AlarmControlHandler alarmControlHandler) {
        return new ServerNotifyConfigBuilder(httpAgent, properties, alarmControlHandler);
    }

    @Bean
    public HippoSendMessageService hippoSendMessageService(NotifyConfigBuilder notifyConfigBuilder,
                                                           AlarmControlHandler alarmControlHandler) {
        return new HippoBaseSendMessageService(notifyConfigBuilder, alarmControlHandler);
    }

    @Bean
    public ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler(HippoSendMessageService hippoSendMessageService) {
        return new ThreadPoolNotifyAlarmHandler(hippoSendMessageService);
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

    @Bean
    public ServerThreadPoolDynamicRefresh threadPoolDynamicRefresh(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler) {
        return new ServerThreadPoolDynamicRefresh(threadPoolNotifyAlarmHandler);
    }

}
