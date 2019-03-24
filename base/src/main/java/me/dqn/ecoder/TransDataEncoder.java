package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/3/24 4:49
 */
public class TransDataEncoder extends MessageToByteEncoder<TransData> {
    Logger logger = LoggerFactory.getLogger(TransDataEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, TransData msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("Encode出错ClientInfo为空");
        out.writeInt(msg.getDataSize());//5
        out.writeInt(msg.getType());//1
        out.writeLong(msg.getSess());//2
        out.writeInt(msg.getFromPort());//3
        out.writeInt(msg.getToPort());//4
        out.writeBytes(msg.getData());
        logger.info("write Index: {}", out.writerIndex());
    }
}
