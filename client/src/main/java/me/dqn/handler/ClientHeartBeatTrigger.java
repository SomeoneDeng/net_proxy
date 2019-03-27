package me.dqn.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 太久不写的话，发一个心跳包
 *
 * @author dqn
 * created at 2019/3/27 22:12
 */
public class ClientHeartBeatTrigger extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ClientHeartBeatTrigger.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                logger.info("太久没写，发个心跳包告诉服务器自己没死。。");
                ctx.writeAndFlush(new TransData.Builder()
                        .type(TransData.TYPE_HT)
                        .dataSize(0)
                        .data(new byte[0])
                        .build());
            }
        }
    }
}
