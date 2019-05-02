package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.client.ClientContext;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Long sess = ClientContext.getINSTANCE().getServerSessMap().get(ctx.channel());
        Channel clientChan = ClientContext.getINSTANCE().getClientFuture().channel();
        clientChan.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPT_DIS)
                .sess(sess)
                .dataSize(0)
                .data(new byte[0])
                .build());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        Long sess = ClientContext.getINSTANCE().getServerSessMap().get(ctx.channel());
        ByteBuf data = (ByteBuf) msg;
        int TOP = 65535;
        int readableBytes = data.readableBytes();
        Channel clientChan = ClientContext.getINSTANCE().getClientFuture().channel();
        byte[] bytes;
        while (TOP < readableBytes){
            bytes = new byte[TOP];
            data.readBytes(bytes);
//            data.release();
            clientChan.writeAndFlush(new TransData.Builder()
                    .type(TransData.TYPE_DT)
                    .sess(sess)
                    .fromPort(1)
                    .toPort(1)
                    .dataSize(bytes.length)
                    .data(bytes)
                    .build());
            readableBytes = data.readableBytes();
        }
        logger.info("last: {}",data.readableBytes());
        bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        data.release();
        clientChan.writeAndFlush(new TransData.Builder()
                .type(TransData.TYPE_DT)
                .sess(sess)
                .fromPort(1)
                .toPort(1)
                .dataSize(bytes.length)
                .data(bytes)
                .build());

    }
}
