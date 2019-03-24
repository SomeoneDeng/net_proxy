package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dqn
 * created at 2019/3/24 4:44
 */
public class TransDataDecoder1 extends LengthFieldBasedFrameDecoder {
    Logger logger = LoggerFactory.getLogger(TransDataDecoder1.class);

    public TransDataDecoder1(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) return null;
        int len = in.readableBytes();
        int size = in.readInt();
        logger.info("size: {},data len: {}", size, len);
        int type = in.readInt();
        long sess = in.readLong();
        int fromPort = in.readInt();
        int toPort = in.readInt();
        byte[] data = new byte[size];
        in.readBytes(data);
        logger.info("after read,readable:{}", in.readableBytes());
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
