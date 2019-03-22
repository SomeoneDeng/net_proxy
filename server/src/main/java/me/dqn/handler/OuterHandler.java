package me.dqn.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理用户请求，tcp转发到client
 *
 * @author dqn
 * created at 2019/3/13 2:51
 */
public class OuterHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(OuterHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("outer port: {}", ctx.channel().localAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
