package me.dqn.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import me.dqn.client.Client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dqn
 * created at 2019/3/23 16:27
 */
public class ClientContext {
    private volatile static ClientContext INSTANCE = null;
    // 根据sess 找到到真实channel
    private ConcurrentHashMap<Long, Channel> serverMap;
    // 根据真实channel找到sess
    private ConcurrentHashMap<Channel, Long> serverSessMap;
    // 到服务端
    private ChannelFuture clientFuture = null;
    private Client client;
    private ClientConfigure clientConfigure;

    private ClientContext() {
        serverMap = new ConcurrentHashMap<>();
        serverSessMap = new ConcurrentHashMap<>();
        clientConfigure = new ClientConfigure("client.yml");
    }

    /**
     * 启动客户端
     */
    public void start() throws InterruptedException {
        client = new Client(clientConfigure.getServerHost(), clientConfigure.getServerPort());
        client.startRegister(clientConfigure.getClients());
    }

    public static ClientContext getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (ClientContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ClientContext();
                }
            }
        }
        return INSTANCE;
    }


    public ConcurrentHashMap<Long, Channel> getServerMap() {
        return serverMap;
    }

    public ChannelFuture getClientFuture() {
        return clientFuture;
    }

    public void setClientFuture(ChannelFuture clientFuture) {
        this.clientFuture = clientFuture;
    }

    public ConcurrentHashMap<Channel, Long> getServerSessMap() {
        return serverSessMap;
    }

    public ClientConfigure getClientConfigure() {
        return clientConfigure;
    }

    public void setClientConfigure(ClientConfigure clientConfigure) {
        this.clientConfigure = clientConfigure;
    }
}
