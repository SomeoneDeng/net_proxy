package me.dqn.traffic;

import io.netty.channel.Channel;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import me.dqn.server.channel.ClientChannelManager;
import me.dqn.util.StateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dqn
 * created at 2019/5/3 1:48
 */
public class ClientChannelCounter extends ChannelTrafficShapingHandler {
    private Logger logger = LoggerFactory.getLogger(ClientChannelCounter.class);
    private Channel channel;

    public ClientChannelCounter(Channel channel, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(writeLimit, readLimit, checkInterval, maxTime);
        this.channel = channel;
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        logger.info("client[{}] wrote bytes: {}, read bytes: {}", channel.id().asShortText(),
                counter.lastWrittenBytes(), counter.lastReadBytes());
        StateInfo stateInfo = ClientChannelManager.clientChannelSpeed.get(channel.id().asShortText());
        stateInfo.setChannelId(channel.id().asShortText());
        stateInfo.setFrom(channel.remoteAddress().toString());
        stateInfo.setWriteSpeed(counter.lastWrittenBytes());
        stateInfo.setReadSpeed(counter.lastReadBytes());
        super.doAccounting(counter);
    }
}
