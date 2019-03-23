package me.dqn.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dqn
 * created at 2019/3/23 16:27
 */
public class ClientManager {
    private volatile static ClientManager INSTANCE = null;
    // 根据sess 找到到真实channel
    private ConcurrentHashMap<Long, Channel> serverMap;
    // 根据真实channel找到sess
    private ConcurrentHashMap<Channel, Long> serverSessMap;
    // 到服务端
    private ChannelFuture clientFuture = null;

    private ClientManager() {
        serverMap = new ConcurrentHashMap<>();
        serverSessMap = new ConcurrentHashMap<>();
    }

    public static ClientManager getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (ClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ClientManager();
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

}
