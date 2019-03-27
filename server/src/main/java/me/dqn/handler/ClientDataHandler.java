package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import me.dqn.channel.ClientChannelManager;
import me.dqn.channel.OuterChannelManager;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册client信息（获取channel）
 *
 * @author dqn
 * created at 2019/3/13 20:06
 */
public class ClientDataHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ClientDataHandler.class);

    /**
     * 从msg中获取响应的客户端信息，包括代理的端口等
     * todo: 注册完成后，加入心跳队伍
     * 注册channel
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) throw new NullPointerException("未收到ClientInfo");
        TransData transData = (TransData) msg;
        if (transData.getType() == TransData.TYPE_DT) {
            // 处理数据
            dispatchData(ctx, transData);
        } else if (transData.getType() == TransData.TYPT_DIS) {
            logger.info("client请求关闭外部连接");
            OuterChannelManager.outerSession.get(transData.getSess()).close();
        }
        ctx.fireChannelRead(msg);
    }

    /**
     * 分发client过来的数据包
     *
     * @param transData
     */
    private void dispatchData(ChannelHandlerContext context, TransData transData) throws InterruptedException {
        // 先拿到Outer channel
        Channel channel = OuterChannelManager.outerSession.get(transData.getSess());
        if (channel != null && channel.isActive()) {
            ByteBuf resp = context.alloc().buffer(transData.getDataSize());
            resp.writeBytes(transData.getData());
            channel.writeAndFlush(resp).sync();
        }
    }


}
