package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.channel.ClientChannelManager;
import me.dqn.channel.OuterChannelManager;
import me.dqn.conf.ServerConfigManager;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 处理用户请求，tcp转发到client
 *
 * @author dqn
 * created at 2019/3/13 2:51
 */
public class OuterHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(OuterHandler.class);
    private final int BATCH_SIZE = 1024 * 1024;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // session
        Long channelSession = Long.valueOf(ctx.channel().id().asShortText(), 16);
        logger.info("outer port: {}, sess:{}", ctx.channel().localAddress(), channelSession);
        OuterChannelManager.outerSession.put(channelSession, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接处理结束");
        long sess = Long.valueOf(ctx.channel().id().asShortText(), 16);
        OuterChannelManager.outerSession.remove(sess);
        // 通知client关闭真实连接
        InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
        int port = ServerConfigManager.portMapping.get(address.getPort());
        Channel clientChannel = ClientChannelManager.getChannel(address.getPort() + ":" + port);
        clientChannel.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPT_DIS)
                .sess(sess)
                .fromPort(port)
                .toPort(address.getPort())
                .dataSize(0)
                .data(new byte[0])
                .build());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
        ByteBuf byteBuf = (ByteBuf) msg;
        Long sessId = Long.valueOf(ctx.channel().id().asShortText(), 16);
        int readableBytes = byteBuf.readableBytes();
        int port = ServerConfigManager.portMapping.get(address.getPort());
        Channel channel = ClientChannelManager.getChannel(address.getPort() + ":" + port);
        byte[] data = new byte[readableBytes];
        byteBuf.readBytes(data);
        logger.info("write to client,length:{}, sess:{}", readableBytes, sessId);
        channel.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPE_DT)
                .sess(sessId)
                .fromPort(port)
                .toPort(address.getPort())
                .dataSize(readableBytes)
                .data(data)
                .build());
    }
}
