package me.dqn.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
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
 * @author dqn
 * created at 2019/3/28 2:54
 */
public class ClientRegisterHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ClientRegisterHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TransData) {
            TransData transData = (TransData) msg;
            if (((TransData) msg).getType() == TransData.TYPE_REG) {
                logger.info("处理客户端注册");
                // 记录channel id
                Server.instance().channelIdToRealPort.put(ctx.channel().id(), transData.getToPort());
                ClientChannelManager.put(transData.getToPort() + ":" + transData.getFromPort(), ctx.channel());
                Server.instance().onlineCount();
                openOuterPort(transData.getToPort());
            }
        }
        ctx.fireChannelRead(msg);
    }

    /**
     * 打开真实端口（未打开的话）
     */
    private void openOuterPort(Integer port) {
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
