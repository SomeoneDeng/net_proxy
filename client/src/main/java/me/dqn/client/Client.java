package me.dqn.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.context.ClientManager;
import me.dqn.ecoder.TransDataDecoder;
import me.dqn.ecoder.TransDataEncoder;
import me.dqn.handler.DataHandler;
import me.dqn.handler.HeartHandler;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/3/22 11:27
 */
public class Client {
    Logger logger = LoggerFactory.getLogger(Client.class);
    private static volatile Client INSTANCE = null;
    private String HOST = "127.0.0.1";
    private Integer PORT = 9999;
    // 代理的端口，届时放在配置文件
    private Integer fromPort = 9001;
    private Integer toPort = 9011;

    private Client() {
    }

    public static Client getInstance() {
        if (INSTANCE == null) {
            synchronized (Client.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Client();
                }
            }
        }
        return INSTANCE;
    }

    public void startRegister() {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        public void initChannel(NioSocketChannel ch) {
                            ch.pipeline()
                                    // TODO: 2019/3/22 加入心跳handler
                                    .addLast(new TransDataEncoder())
                                    .addLast(new TransDataDecoder(
                                            1024*1024,
                                            20,
                                            4,
                                            0,
                                            0))
//                                    .addLast(new HeartHandler())
                                    .addLast(new DataHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info("连接成功");
                } else {
                    logger.info("连接失败");
                }
            });
            // 注册
            registerToServer(future);
            ClientManager.getINSTANCE().setClientFuture(future);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void registerToServer(ChannelFuture future) {
        future.channel().writeAndFlush(new TransData.Builder()
                .type(TransData.TYPE_REG)
                .sess(0)
                .fromPort(fromPort)
                .toPort(toPort)
                .dataSize(0)
                .data(new byte[0])
                .build());
    }
}
