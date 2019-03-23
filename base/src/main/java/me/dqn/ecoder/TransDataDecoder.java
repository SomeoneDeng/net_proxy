package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author dqn
 * created at 2019/3/24 4:44
 */
public class TransDataDecoder extends LengthFieldBasedFrameDecoder {
    Logger logger = LoggerFactory.getLogger(TransDataDecoder.class);

    public TransDataDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) return null;
        int len = in.readableBytes();
        int type = in.readInt();
        long sess = in.readLong();
        int fromPort = in.readInt();
        int toPort = in.readInt();
        int size = in.readInt();
        byte[] data = null;
        if (type == TransData.TYPE_DT) {
            logger.info("size: {},data len: {}", size, len);
            ByteBuf buf = in.readBytes(size);
            data = new byte[buf.readableBytes()];
            buf.readBytes(data);
        }
        return new TransData.Builder()
                .type(type)
                .sess(sess)
                .fromPort(fromPort)
                .toPort(toPort)
                .dataSize(size)
                .data(data)
                .build();
    }

}
