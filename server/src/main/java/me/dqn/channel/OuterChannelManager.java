package me.dqn.channel;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于管理外部端口channel
 *
 * @author dqn
 * created at 2019/3/14 18:49
 */
public class OuterChannelManager {
    private static ConcurrentHashMap<Integer, Channel> outerChannels = new ConcurrentHashMap<>();
    ;

    public static Channel getChannel(Integer port) {
        return outerChannels.get(port);
    }

    public static void puChannel(Integer port, Channel ch) {
        outerChannels.put(port, ch);
    }

    public static boolean exists(Integer port) {
        return outerChannels.contains(port);
    }

    public static ConcurrentHashMap<Integer, Channel> getOuterChannels() {
        return outerChannels;
    }
}
