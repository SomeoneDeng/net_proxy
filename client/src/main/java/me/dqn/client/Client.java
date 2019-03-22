package me.dqn.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.ecoder.ClientInfoDecoder;
import me.dqn.ecoder.ClientInfoEncoder;
import me.dqn.handler.DataHandler;
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
    private Integer fromPort = 3306;
    private Integer toPort = 3307;

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
                                    .addLast(new ClientInfoEncoder())
                                    .addLast(new ClientInfoDecoder(1024 * 1024, 0, 4))
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

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void registerToServer(ChannelFuture future) {
        TransData transData = new TransData();
        transData.setType(1);
        transData.setToPort(toPort);
        transData.setFromPort(fromPort);
        transData.setData(null);
        transData.setSess(0);
        future.channel().writeAndFlush(transData);
    }
}
