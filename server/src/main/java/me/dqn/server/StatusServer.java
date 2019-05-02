package me.dqn.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;
import me.dqn.handler.StatusHandler;
import me.dqn.handler.status.ClientHandler;
import me.dqn.handler.status.OuterHandler;
import me.dqn.interfaces.HttpHandle;
import me.dqn.util.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dqn
 * created at 2019/4/11 12:26
 */
public class StatusServer {

    private Logger logger = LoggerFactory.getLogger(StatusServer.class);

    private ServerBootstrap bootstrap;
    private ServerConfig config;
    private Map<String, HttpHandle> handlers = new ConcurrentHashMap<>();

    public StatusServer(ServerConfig configManager) {
        this.bootstrap = new ServerBootstrap();
        this.config = configManager;
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init() throws InterruptedException {
        initHandler();
        this.bootstrap.handler(new LoggingHandler())
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpRequestDecoder())
                                .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                .addLast(new HttpResponseEncoder())
                                .addLast(new StatusHandler(handlers));
                    }
                })
                .bind(config.getStatusPort()).
                addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("启动服务状态监控成功");
                    }
                });
    }

    private void initHandler() {
        handlers.put("/api/client/status", new ClientHandler());
        handlers.put("/api/outer/status", new OuterHandler());
    }
}
