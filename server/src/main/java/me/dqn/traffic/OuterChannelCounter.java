package me.dqn.traffic;

import io.netty.channel.Channel;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import me.dqn.server.channel.OuterChannelManager;
import me.dqn.util.StateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/5/3 0:52
 */
public class OuterChannelCounter extends ChannelTrafficShapingHandler {
    Logger logger = LoggerFactory.getLogger(OuterChannelCounter.class);
    private Channel channel;

    public OuterChannelCounter(Channel channel, long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(writeLimit, readLimit, checkInterval, maxTime);
        this.channel = channel;
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        logger.info("outer[{}] wrote bytes: {}, read bytes: {}", channel.id().asShortText(),
                counter.lastWrittenBytes(), counter.lastReadBytes());
        StateInfo stateInfo = OuterChannelManager.outerChannelSpeed.get(channel.id().asShortText());
        stateInfo.setFrom(channel.remoteAddress().toString());
        stateInfo.setReadSpeed(counter.lastReadBytes());
        stateInfo.setWriteSpeed(counter.lastWrittenBytes());
        stateInfo.setChannelId(channel.id().asShortText());
        stateInfo.setTime(counter.lastTime());
        super.doAccounting(counter);
    }
}
