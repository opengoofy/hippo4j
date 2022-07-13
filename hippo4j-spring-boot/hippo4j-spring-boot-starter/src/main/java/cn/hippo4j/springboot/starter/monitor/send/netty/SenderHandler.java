package cn.hippo4j.springboot.starter.monitor.send.netty;

import cn.hippo4j.common.monitor.MessageWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SenderHandler
 *
 * @author lk
 * @date 2022/06/18
 */
@Slf4j
@AllArgsConstructor
public class SenderHandler extends SimpleChannelInboundHandler<MessageWrapper> {

    private MessageWrapper messageWrapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageWrapper msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(messageWrapper);
    }
}
