package me.dqn.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import me.dqn.ecoder.TransDataDecoder;
import me.dqn.ecoder.TransDataEncoder;
import me.dqn.handler.ClientHeartBeatTrigger;
import me.dqn.handler.DataHandler;
import me.dqn.handler.ServerHandler;
import me.dqn.protocol.TransData;
import me.dqn.util.ClientConfigure;
import me.dqn.util.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dqn
 * created at 2019/3/22 11:27
 */
public class Client {
    private String HOST;
    private int PORT;
    private ChannelFuture future;
    private List<ClientInfo> clientInfos;

    public Bootstrap clientBootStrap;

    public Client(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;

        clientBootStrap = new Bootstrap();
        clientBootStrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    public void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
    }

    Logger logger = LoggerFactory.getLogger(Client.class);

    public void startRegister(ClientConfigure clientConfigure) {
        this.clientInfos = clientConfigure.getClients();
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        public void initChannel(NioSocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new IdleStateHandler(0, clientConfigure.getHeartbeatTime(), 0, TimeUnit.SECONDS))
                                    .addLast(new LengthFieldPrepender(4, false))
                                    .addLast(new TransDataEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new TransDataDecoder())
                                    .addLast(new ClientHeartBeatTrigger())
                                    .addLast(new DataHandler(Client.this));
                        }
                    });
            // TODO: 2019/4/20 客户端为每一个connect开一个channel，避免卡住
            future = b.connect(HOST, PORT).sync();
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info("[{}]连接成功", HOST);
                } else {
                    logger.info("[{}]连接失败", HOST);
                }
            });
            // 注册
            clientInfos.forEach(this::registerToServer);
            ClientContext.getINSTANCE().setClientFuture(future);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != future) {
                if (future.channel() != null && future.channel().isOpen()) {
                    future.channel().close();
                }
            }
            logger.info("与服务器连接断开，准备重连");
            startRegister(clientConfigure);
            logger.info("重连成功");
        }
    }

    private void registerToServer(ClientInfo clientInfo) {
        future.channel()
                .writeAndFlush(new TransData.Builder()
                        .type(TransData.TYPE_REG)
                        .sess(0)
                        .fromPort(clientInfo.getFromPort())
                        .toPort(clientInfo.getToPort())
                        .dataSize(0)
                        .data(new byte[0])
                        .build());
    }

    public List<ClientInfo> getClientInfos() {
        return clientInfos;
    }
}
