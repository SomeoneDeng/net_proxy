package me.dqn.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理心跳信息
 *
 * @author dqn
 * created at 2019/3/27 21:10
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("heartbeat server active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TransData) {
            if (((TransData) msg).getType() == TransData.TYPE_HT) {
                logger.info("收到心跳信号，from {}", ctx.channel().remoteAddress());
                // 回应
                ctx.channel().writeAndFlush(new TransData.Builder()
                        .type(TransData.TYPE_HT)
                        .dataSize(0)
                        .data(new byte[0])
                        .build());
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("{} 心跳异常退出，{}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
