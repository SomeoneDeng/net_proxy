package me.dqn;

import ch.qos.logback.core.net.server.Client;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.ecoder.ClientInfoDecoder;
import me.dqn.ecoder.ClientInfoEncoder;
import me.dqn.protocol.ClientInfo;

/**
 * @author dqn
 * created at 2019/3/12 1:48
 */
public class App {

    private static String HOST = "127.0.0.1";
    private static Integer PORT = 9999;



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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientInfoEncoder());
//                            ch.pipeline().addLast();
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setKey(12);
            clientInfo.setPort(3306);
            future.channel().writeAndFlush(clientInfo);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
