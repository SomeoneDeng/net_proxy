package me.dqn;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import me.dqn.ecoder.ClientInfoDecoder;
import me.dqn.ecoder.ClientInfoEncoder;
import me.dqn.handler.ClientRegisterHandler;
import me.dqn.util.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/3/12 1:36
 */
public class App {
    private static ConfigManager configManager;
    private static ServerBootstrap registerBootstrap;
    private static ServerBootstrap OuterBootstrap;
    static Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws InterruptedException {
        initConfig();
        startRegister();
    }


    /**
     * load config from yml file
     */
    private static void initConfig() {
        configManager = new ConfigManager("server.yml");
    }

    private static void startRegister() throws InterruptedException {
        logger.info("starting register server....");
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
