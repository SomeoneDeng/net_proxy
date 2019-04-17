package me.dqn.traffic;

import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import me.dqn.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 检测传输速度
 *
 * @author dqn
 * created at 2019/4/17 17:54
 */
public class OuterTrafficCounter extends GlobalChannelTrafficShapingHandler {
    Logger logger = LoggerFactory.getLogger(OuterTrafficCounter.class);

    Integer port;

    public OuterTrafficCounter(ScheduledExecutorService executor, long checkInterval, Integer port) {
        super(executor, checkInterval);
        this.port = port;
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        if (counter == null) return;
        long totalRead = counter.cumulativeReadBytes();
        long totalWrite = counter.cumulativeWrittenBytes();
        long deltaTime = counter.checkInterval();
        Long lastRead = Server.instance().lastOuterRead.get(port);
        Long lastWrite = Server.instance().lastOuterWrite.get(port);
        if (lastWrite == null) lastWrite = 0L;
        if (lastRead == null) lastRead = 0L;
        double readSpeed = ((totalRead - lastRead) / 1024) / (deltaTime / 1000);
        double writeSpeed = ((totalWrite - lastWrite) / 1024) / (deltaTime / 1000);
        Server.instance().lastOuterRead.put(port, totalRead);
        Server.instance().lastOuterWrite.put(port, totalWrite);
        Server.instance().outerReadSpeed.put(port, readSpeed);
        Server.instance().outerWriteSpeed.put(port, writeSpeed);
//        logger.info("port [{}],read[{}KB/S],write[{}KB/S]", port, readSpeed, writeSpeed);
        super.doAccounting(counter);
    }
}
