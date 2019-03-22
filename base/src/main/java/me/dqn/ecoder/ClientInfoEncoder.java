package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.dqn.protocol.TransData;

/**
 * @author dqn
 * created at 2019/3/13 19:32
 */
public class ClientInfoEncoder extends MessageToByteEncoder<TransData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TransData msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("Encode出错ClientInfo为空");
        out.writeInt(msg.getType());
        out.writeInt(msg.getFromPort());
        out.writeInt(msg.getToPort());
        out.writeInt(msg.getDataSize());
        if (msg.getType() == TransData.TYPE_DT) {
            out.writeBytes(msg.getData());
        }
    }
}
