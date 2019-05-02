package me.dqn.server.channel;

import io.netty.channel.Channel;
import me.dqn.util.StateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放client channel
 *
 * @author dqn
 * created at 2019/3/13 20:11
 */
public class ClientChannelManager {
    private static Logger logger = LoggerFactory.getLogger(ClientChannelManager.class);

    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>(32);
    public static ConcurrentHashMap<String, StateInfo> clientChannelSpeed = new ConcurrentHashMap<>();

    public static void put(String key, Channel channel) {
        channelMap.put(key, channel);
    }

    public static Channel getChannel(String key) {
        return channelMap.get(key);
    }

    public static void removeChannel(Channel chan) {
        // 关闭外部端口
        channelMap.forEach((k, v) -> {
            if (chan == v) {
                logger.info("由于client 【{}】断开，关闭Outer端口【{}】", chan.remoteAddress(), k.split(":")[0]);
                OuterChannelManager.closeOuterPort(Integer.parseInt(k.split(":")[0]));
            }
        });
    }

    public static Integer size() {
        return channelMap.size();
    }
}
