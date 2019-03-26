package me.dqn.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.channel.ClientChannelManager;
import me.dqn.channel.OuterChannelManager;
import me.dqn.conf.ServerConfigManager;
import me.dqn.protocol.TransData;
import me.dqn.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册client信息（获取channel）
 *
 * @author dqn
 * created at 2019/3/13 20:06
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    /**
     * 从msg中获取响应的客户端信息，包括代理的端口等
     * todo: 注册完成后，加入心跳队伍
     * 注册channel
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) throw new NullPointerException("未收到ClientInfo");
        TransData transData = (TransData) msg;
        // 处理注册
        if (transData.getType() == TransData.TYPE_REG) {
            registryClient(ctx, transData);
        } else if (transData.getType() == TransData.TYPE_DT) {
            // 处理数据
            dispatchData(ctx, transData);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        int realPort = Server.instance().channelIdToRealPort.get(ctx.channel().id());
        logger.info("与客户端的连接断开，代理的端口为:{},被代理的端口为：{}", realPort, ServerConfigManager.portMapping.get(realPort));
        ClientChannelManager.removeChannel(realPort + ":" + ServerConfigManager.portMapping.get(realPort), ctx.channel());
        Server.instance().onlineCount();
    }


    /**
     * 处理客户端注册
     *
     * @param ctx
     * @param transData
     */
    private void registryClient(ChannelHandlerContext ctx, TransData transData) {
        logger.info("处理客户端注册");
        // 记录channel id
        Server.instance().channelIdToRealPort.put(ctx.channel().id(), transData.getToPort());
        ClientChannelManager.put(transData.getToPort() + ":" + transData.getFromPort(), ctx.channel());
        Server.instance().onlineCount();
        openOuterPort(transData.getToPort());
    }

    /**
     * 分发client过来的数据包
     *
     * @param transData
     */
    private void dispatchData(ChannelHandlerContext context, TransData transData) throws InterruptedException {
        // 先拿到Outer channel
        Channel channel = OuterChannelManager.outerSession.get(transData.getSess());
        if (channel != null && channel.isActive()) {
            logger.info("from client sess: {}，to user，{}",transData.getSess(), channel.id());
            ByteBuf resp = context.alloc().buffer(transData.getDataSize());
            resp.writeBytes(transData.getData());
            channel.writeAndFlush(resp).sync();
        }
    }


    /**
     * 打开真实端口（未打开的话）
     */
    private void openOuterPort(Integer port) {
        // TODO: 2019/3/22  client 断开连接，这个要关了相应端口
        if (!OuterChannelManager.exists(port)) {
            ServerBootstrap bootstrap = new ServerBootstrap();
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup();
            ChannelFuture future = bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new OuterHandler());
                        }
                    }).bind(port);
            OuterChannelManager.putChannel(port, future);
            logger.info("端口 {} 已打开, 对应内网端口：{}", port, ServerConfigManager.portMapping.get(port));
        }
    }
}
