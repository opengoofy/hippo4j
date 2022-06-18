package cn.hippo4j.console.netty;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.toolkit.MessageConvert;
import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ServerHandler
 *
 * @author lk
 * @date 2022/06/18
 */
@Slf4j
@AllArgsConstructor
public class ServerHandler extends SimpleChannelInboundHandler<MessageWrapper> {

    private QueryMonitorExecuteChoose queryMonitorExecuteChoose;

    private ThreadPoolTaskExecutor monitorThreadPoolTaskExecutor;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {
        Runnable task = () -> {
            Message message = MessageConvert.convert(msg);
            queryMonitorExecuteChoose.chooseAndExecute(message);
        };
        try {
            monitorThreadPoolTaskExecutor.execute(task);
        } catch (Exception ex) {
            log.error("Monitoring data insertion database task overflow.", ex);
        }
    }
}
