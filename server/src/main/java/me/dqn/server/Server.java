package me.dqn.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.dqn.ServerApp;
import me.dqn.channel.ClientChannelManager;
import me.dqn.conf.ServerConfigManager;
import me.dqn.ecoder.TransDataDecoder;
import me.dqn.ecoder.TransDataEncoder;
import me.dqn.handler.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dqn
 * created at 2019/3/22 8:11
 */
public class Server {

    private volatile static Server INSTANCE = null;

    private ServerConfigManager configManager;
    private ServerBootstrap registerBootstrap;
    private ServerBootstrap OuterBootstrap;
    static Logger logger = LoggerFactory.getLogger(ServerApp.class);
    // 存放client channel id 和 对应的端口号
    public Map<ChannelId, Integer> channelIdToRealPort;

    private Server() {
        channelIdToRealPort = new ConcurrentHashMap<>();
    }

    /**
     * load config from .yml file
     */
    private void initConfig() {
        configManager = new ServerConfigManager("server.yml");
    }

    /**
     * 启动完成后，客户端可以注册自己，建立channel
     *
     * @throws InterruptedException
     */
    private void startRegister() throws InterruptedException {
        logger.info("启动注册服务");
        registerBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        registerBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast(new TransDataEncoder())
                                .addLast(new TransDataDecoder(
                                        1024*1024,
                                        20,
                                        4,
                                        0,
                                        0))
                                .addLast(new ClientHandler());
                    }
                })
                .bind(configManager.getRegisterPort())
                .sync();
    }

    public void start() throws InterruptedException {
        initConfig();
        startRegister();
    }

    public void onlineCount() {
        logger.info("在线client数：{}", ClientChannelManager.size());
    }

    public static Server instance() {
        if (INSTANCE == null) {
            synchronized (Server.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Server();
                }
            }
        }
        return INSTANCE;
    }
}
