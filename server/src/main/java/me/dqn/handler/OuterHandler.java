package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.protocol.TransData;
import me.dqn.server.channel.ClientChannelManager;
import me.dqn.server.channel.OuterChannelManager;
import me.dqn.traffic.OuterChannelCounter;
import me.dqn.util.ServerConfig;
import me.dqn.util.StateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * 处理用户请求，tcp转发到client
 *
 * @author dqn
 * created at 2019/3/13 2:51
 */
public class OuterHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(OuterHandler.class);
    private final int BATCH_SIZE = 1024 * 1024;
    private static final String TRAFFIC_COUNTER = "trafficcounter";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // session
        // 添加流量监控
        // 1 * 1024 * 1024 * 1024
        long limit = 1073741824;
        ctx.pipeline().addFirst(TRAFFIC_COUNTER, new OuterChannelCounter(ctx.channel(), limit, limit, 1000, 30000));
        OuterChannelManager.outerChannelSpeed.put(ctx.channel().id().asShortText(), new StateInfo());
        Long channelSession = Long.valueOf(ctx.channel().id().asShortText(), 16);
        OuterChannelManager.outerSession.put(channelSession, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        removeCounterIfExist(ctx);
        long sess = Long.valueOf(ctx.channel().id().asShortText(), 16);
        OuterChannelManager.outerSession.remove(sess);
        // 通知client关闭真实连接
        InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
        int port = ServerConfig.portMapping.get(address.getPort());
        Channel clientChannel = ClientChannelManager.getChannel(address.getPort() + ":" + port);
        clientChannel.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPT_DIS)
                .sess(sess)
                .fromPort(port)
                .toPort(address.getPort())
                .dataSize(0)
                .data(new byte[0])
                .build());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
        ByteBuf byteBuf = (ByteBuf) msg;
        Long sessId = Long.valueOf(ctx.channel().id().asShortText(), 16);
        int readableBytes = byteBuf.readableBytes();
        int port = ServerConfig.portMapping.get(address.getPort());
        Channel channel = ClientChannelManager.getChannel(address.getPort() + ":" + port);
        byte[] data = new byte[readableBytes];
        byteBuf.readBytes(data);
        byteBuf.release();
        channel.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPE_DT)
                .sess(sessId)
                .fromPort(port)
                .toPort(address.getPort())
                .dataSize(readableBytes)
                .data(data)
                .build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        logger.info("连接出现异常，{}",cause.getMessage());
        removeCounterIfExist(ctx);
        long sess = Long.valueOf(ctx.channel().id().asShortText(), 16);
        OuterChannelManager.outerSession.remove(sess);

    }

    private void removeCounterIfExist(ChannelHandlerContext context) {
        ChannelHandler handler = context.pipeline().get(TRAFFIC_COUNTER);
        if (handler != null) {
            OuterChannelManager.outerChannelSpeed.remove(context.channel().id().asShortText());
            context.pipeline().remove(TRAFFIC_COUNTER);
        }
    }
}
