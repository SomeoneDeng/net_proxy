package me.dqn;

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

import java.util.Date;

/**
 * @author dqn
 * created at 2019/3/12 1:48
 */
public class ClientApp {
    static Logger logger = LoggerFactory.getLogger(ClientApp.class);

    private static String HOST = "127.0.0.1";
    private static Integer PORT = 9999;
    // 代理的端口，届时放在配置文件
    private static Integer fromPort = 3306;
    private static Integer toPort = 3307;


    public static void main(String[] args) throws InterruptedException {
        startRegister();
    }

    private static void startRegister() {
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
                        public void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientInfoEncoder());
                            ch.pipeline().addLast(new ClientInfoDecoder(1024 * 1024, 0, 4));
                            ch.pipeline().addLast(new DataHandler());
                        }
                    });


            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()){
                    logger.info("连接成功");
                }else {
                    logger.info("连接失败");
                }
            });

            TransData transData = new TransData();
            transData.setType(1);
            transData.setToPort(toPort);
            transData.setFromPort(fromPort);
            transData.setData(null);
            future.channel().writeAndFlush(transData);

            for (int i = 0; i < 3; i++) {
                String s = new Date().toString();
                TransData dataInfo = new TransData.Builder().type(TransData.TYPE_DT)
                        .fromPort(fromPort)
                        .toPort(toPort)
                        .dataSize(s.getBytes().length)
                        .data(s.getBytes()).build();
//                logger.info("writing data: {}", new String(dataInfo.getData()));
                future.channel().writeAndFlush(dataInfo).sync();
//                Thread.sleep(1000);
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
