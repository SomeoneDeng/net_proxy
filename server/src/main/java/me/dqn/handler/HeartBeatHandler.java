package me.dqn.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.protocol.TransData;
import me.dqn.server.channel.ClientChannelManager;
import me.dqn.traffic.ClientChannelCounter;
import me.dqn.util.StateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 处理心跳信息
 *
 * @author dqn
 * created at 2019/3/27 21:10
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);
    private static final String CLIENT_TRAFFIC_COUNTER = "clienttrafficcounter";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("heartbeat server active");
        long limit = 1073741824;
        ctx.pipeline().addFirst(CLIENT_TRAFFIC_COUNTER, new ClientChannelCounter(ctx.channel(), limit, limit, 1000, 30000));
        ClientChannelManager.clientChannelSpeed.put(ctx.channel().id().asShortText(), new StateInfo());
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
        removeCounterIfExist(ctx);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("与客户端【{}】的连接断开", ctx.channel().remoteAddress());
        ClientChannelManager.removeChannel(ctx.channel());
        removeCounterIfExist(ctx);
    }

    private void removeCounterIfExist(ChannelHandlerContext context) {
        ChannelHandler handler = context.pipeline().get(CLIENT_TRAFFIC_COUNTER);
        if (handler != null) {
            ClientChannelManager.clientChannelSpeed.remove(context.channel().id().asShortText());
            context.pipeline().remove(CLIENT_TRAFFIC_COUNTER);
        }
    }
}
