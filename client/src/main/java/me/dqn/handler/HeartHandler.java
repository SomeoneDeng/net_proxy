package me.dqn.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.context.ClientManager;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 心跳处理
 *
 * @author dqn
 * created at 2019/3/23 16:18
 */
public class HeartHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(HeartHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TransData) {
            // do something
            if (TransData.TYPE_HT == ((TransData) msg).getType()) {
                logger.info("收到心跳信息");
            }
        }
        ctx.fireChannelRead(msg);
    }
}
