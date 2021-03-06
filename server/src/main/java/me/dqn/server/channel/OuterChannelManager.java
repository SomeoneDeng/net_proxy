package me.dqn.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于管理外部端口channel
 *
 * @author dqn
 * created at 2019/3/14 18:49
 */
public class OuterChannelManager {
    // 打开的端口
    private static ConcurrentHashMap<Integer, ChannelFuture> outerFutures = new ConcurrentHashMap<>();
    // 每个外部链接都有，key是channel id，内部的client共用这个key, id->channel
    public static ConcurrentHashMap<Long, Channel> outerSession = new ConcurrentHashMap<>();
    // channel -> id
    public static ConcurrentHashMap<Channel, Long> outerSessionMap = new ConcurrentHashMap<>();
    // 记录每个channel当前读写速度
    public static ConcurrentHashMap<Channel, Map<String, Long>> outerChannelSpeed = new ConcurrentHashMap<>();

    public static ChannelFuture getChannel(Integer port) {
        return outerFutures.get(port);
    }

    public static void closeOuterPort(int port) {
        ChannelFuture channelFuture = outerFutures.get(port);
        channelFuture.channel().close();
    }

    public static void putChannel(Integer port, ChannelFuture ch) {
        outerFutures.put(port, ch);
    }

    public static boolean exists(Integer port) {
        return outerFutures.contains(port);
    }

    public static ConcurrentHashMap<Integer, ChannelFuture> getOuterChannels() {
        return outerFutures;
    }
}
