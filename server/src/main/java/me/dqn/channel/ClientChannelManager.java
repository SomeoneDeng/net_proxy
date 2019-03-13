package me.dqn.channel;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放channel
 *
 * @author dqn
 * created at 2019/3/13 20:11
 */
public class ClientChannelManager {
    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>(32);

    public static void put(String key, Channel channel) {
        channelMap.put(key, channel);
    }

    public static Channel getChannel(String key) {
        return channelMap.get(key);
    }

    public static Integer size() {
        return channelMap.size();
    }
}
