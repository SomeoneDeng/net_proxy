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

    public static void removeChannel(String key, Channel chan) {
        // 关闭外部端口
        OuterChannelManager.closeOuterPort(Integer.parseInt(key.split(":")[0]));
        channelMap.remove(key, chan);
    }

    public static Integer size() {
        return channelMap.size();
    }
}
