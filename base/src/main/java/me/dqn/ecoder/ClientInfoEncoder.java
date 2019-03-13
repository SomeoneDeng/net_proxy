package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.dqn.protocol.ClientInfo;

/**
 * @author dqn
 * created at 2019/3/13 19:32
 */
public class ClientInfoEncoder extends MessageToByteEncoder<ClientInfo> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ClientInfo msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("Encode出错ClientInfo为空");
        out.writeInt(msg.getKey());
        out.writeInt(msg.getPort());
    }
}
