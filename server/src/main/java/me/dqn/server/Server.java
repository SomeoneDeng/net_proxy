package me.dqn.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import me.dqn.ServerApp;
import me.dqn.ecoder.TransDataDecoder;
import me.dqn.ecoder.TransDataEncoder;
import me.dqn.handler.ClientDataHandler;
import me.dqn.handler.ClientRegisterHandler;
import me.dqn.handler.HeartBeatHandler;
import me.dqn.handler.HeartTrigger;
import me.dqn.server.channel.ClientChannelManager;
import me.dqn.traffic.ClientTrafficCounter;
import me.dqn.util.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author dqn
 * created at 2019/3/22 8:11
 */
public class Server {

    private volatile static Server INSTANCE = null;

    private ServerConfig configManager;
    private ServerBootstrap registerBootstrap;
    private GlobalChannelTrafficShapingHandler trafficShapingHandler = null;
    static Logger logger = LoggerFactory.getLogger(ServerApp.class);
    // 存放client channel id 和 对应的端口号
    public Map<ChannelId, Integer> channelIdToRealPort;

    // 读取的客户端数据量
    public long lastReadBytes = 0;
    public long lastWriteBytes = 0;
    // 当前读取速度
    public double readSpeedFromClient = 0;
    public double writeSpeedToClient = 0;
    // 外部读取速度(Outer 有多个)
    public Map<Integer, Long> lastOuterRead = new HashMap<>();
    public Map<Integer, Double> outerReadSpeed = new HashMap<>();
    // 外部写入速度
    public Map<Integer, Long> lastOuterWrite = new HashMap<>();
    public Map<Integer, Double> outerWriteSpeed = new HashMap<>();


    private Server() {
        channelIdToRealPort = new ConcurrentHashMap<>();
    }

    /**
     * load config from .yml file
     */
    private void initConfig() {
        configManager = new ServerConfig("server.yml");
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
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator())
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast(trafficShapingHandler)
                                // 10秒内没`读`操作断开连接
                                .addLast(new IdleStateHandler(configManager.getHeartBeatTime(), 0, 0, TimeUnit.SECONDS))
                                .addLast(new HeartTrigger())
                                .addLast(new LengthFieldPrepender(4, false))
                                .addLast(new TransDataEncoder())
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast(new TransDataDecoder())
                                .addLast(new HeartBeatHandler())
                                .addLast(new ClientRegisterHandler())
                                .addLast(new ClientDataHandler());
                    }
                });
        trafficShapingHandler = new ClientTrafficCounter(registerBootstrap.config().childGroup(), 1000);
        registerBootstrap.bind(configManager.getRegisterPort()).sync();
    }

    public void start() throws InterruptedException {
        initConfig();
        new StatusServer(configManager);
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
