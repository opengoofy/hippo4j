package cn.hippo4j.config.netty;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.toolkit.MessageConvert;
import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
import cn.hippo4j.config.service.biz.HisRunDataService;
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

    private HisRunDataService hisRunDataService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {
        hisRunDataService.dataCollect(msg);
    }
}
