package me.dqn.traffic;

import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import me.dqn.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 检测和client的速度
 *
 * @author dqn
 * created at 2019/4/17 18:02
 */
public class ClientTrafficCounter extends GlobalChannelTrafficShapingHandler {
    Logger logger = LoggerFactory.getLogger(ClientTrafficCounter.class);

    public ClientTrafficCounter(ScheduledExecutorService executor, long checkInterval) {
        super(executor, checkInterval);
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        if (counter == null) return;
        long totalRead = counter.cumulativeReadBytes();
        long totalWrite = counter.cumulativeWrittenBytes();
        long deltaTime = counter.checkInterval();
        Long lastRead = Server.instance().lastReadBytes;
        Long lastWrite = Server.instance().lastWriteBytes;
        double readSpeed = ((Math.abs(totalRead - lastRead)) / 1024) / (deltaTime / 1000);
        double writeSpeed = ((Math.abs(totalWrite - lastWrite)) / 1024) / (deltaTime / 1000);
        Server.instance().lastReadBytes = totalRead;
        Server.instance().lastWriteBytes = totalWrite;
        Server.instance().readSpeedFromClient = readSpeed;
        Server.instance().writeSpeedToClient = writeSpeed;
//        logger.info("client: read[{}KB/S],write[{}KB/S]", readSpeed, writeSpeed);
        super.doAccounting(counter);
    }
}
