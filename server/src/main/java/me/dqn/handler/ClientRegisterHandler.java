package me.dqn.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.dqn.channel.ClientChannelManager;
import me.dqn.protocol.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册client信息（获取channel）
 *
 * @author dqn
 * created at 2019/3/13 20:06
 */
public class ClientRegisterHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(ClientRegisterHandler.class);

    /**
     * 从msg中获取响应的客户端信息，包括代理的端口等
     * 注册channel
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("reading...");
        if (msg == null) throw new NullPointerException("未收到ClientInfo,注册失败");
        ClientInfo clientInfo = (ClientInfo) msg;
        ClientChannelManager.put(clientInfo.getKey() + ":" + clientInfo, ctx.channel());
        logger.info("channel map: {}", ClientChannelManager.size());
    }

}
