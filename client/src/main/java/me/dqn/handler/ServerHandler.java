package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.context.ClientManager;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 接受真实服务器的数据
 *
 * @author dqn
 * created at 2019/3/23 16:52
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ServerHandler.class);


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("断开连接,{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        Long sess = ClientManager.getINSTANCE().getServerSessMap().get(ctx.channel());
        ByteBuf data = (ByteBuf) msg;
        int readableBytes = data.readableBytes();
        Channel clientChan = ClientManager.getINSTANCE().getClientFuture().channel();
        byte[] bytes = new byte[readableBytes];
        data.readBytes(bytes);
        clientChan.flush();
        clientChan.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPE_DT)
                .sess(sess)
                .fromPort(1)
                .toPort(1)
                .dataSize(bytes.length)
                .data(bytes)
                .build()).sync();
    }
}
