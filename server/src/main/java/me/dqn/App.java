package me.dqn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.conf.ServerConfigManager;
import me.dqn.ecoder.ClientInfoDecoder;
import me.dqn.handler.ClientRegisterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/3/12 1:36
 */
public class App {
    private static ServerConfigManager configManager;
    private static ServerBootstrap registerBootstrap;
    private static ServerBootstrap OuterBootstrap;
    static Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws InterruptedException {
        initConfig();
        startRegister();
    }


    /**
     * load config from .yml file
     */
    private static void initConfig() {
        configManager = new ServerConfigManager("server.yml");
    }

    /**
     * 启动完成后，客户端可以注册自己，建立channel
     *
     * @throws InterruptedException
     */
    private static void startRegister() throws InterruptedException {
        logger.info("启动注册服务");
        registerBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        registerBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ClientInfoDecoder(1024 * 1024, 0, 4));
                        ch.pipeline().addLast(new ClientRegisterHandler());
                    }
                })
                .bind(configManager.getRegisterPort())
                .sync();
    }
}
