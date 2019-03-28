package me.dqn.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.client.Client;
import me.dqn.client.ClientContext;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理server过来的数据
 *
 * @author dqn
 * created at 2019/3/22 8:52
 */
public class DataHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(DataHandler.class);
    Client thisClient;

    public DataHandler(Client thisClient) {
        this.thisClient = thisClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            throw new NullPointerException("未收到ClientInfo");
        }
        if (msg instanceof TransData) {
            TransData transData = (TransData) msg;
            switch (transData.getType()) {
                case TransData.TYPE_DT:
                    handlerData(ctx, transData);
                    break;
                case TransData.TYPT_DIS:
                    logger.info("关闭session：{}", transData.getSess());
                    break;
                case TransData.TYPE_HT:
                    logger.info("服务器心跳回执");
                    break;
                default:
                    break;
            }
        }
    }


    private void handlerData(ChannelHandlerContext ctx, TransData transData) {
        // 真实channel，没有就创建
        Channel serverChan = ClientContext.getINSTANCE().getServerMap().get(transData.getSess());
        // 创建连接可能出错
        try {
            serverChan = createChannelFuture(ctx, transData, serverChan);
            ByteBuf byteBuf = ctx.alloc().directBuffer(transData.getDataSize());
            byteBuf.writeBytes(transData.getData());
            serverChan.pipeline().writeAndFlush(byteBuf);
        } catch (Exception e) {
            logger.info("连接真实服务器失败,断开外部连接");
            ctx.channel().writeAndFlush(new TransData.Builder()
                    .type(TransData.TYPT_DIS)
                    .sess(transData.getSess())
                    .dataSize(0)
                    .data(new byte[0])
                    .build());
        }
    }

    private Channel createChannelFuture(ChannelHandlerContext ctx, TransData transData, Channel serverChan) throws InterruptedException {
        if (serverChan == null) {
            logger.info("创建到真实服务的连接,port:{}", transData.getFromPort());
            Bootstrap bootstrap = new Bootstrap();
            serverChan = bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    }).connect(thisClient.getClientInfos()
                                    .stream().filter(clientMeta -> clientMeta.getFromPort() == transData.getFromPort())
                                    .findFirst()
                                    .get().getServiceHost(),
                            transData.getFromPort())
                    .sync().channel();
            ClientContext.getINSTANCE().getServerMap().put(transData.getSess(), serverChan);
            ClientContext.getINSTANCE().getServerSessMap().put(serverChan, transData.getSess());
        }
        return serverChan;
    }
}
