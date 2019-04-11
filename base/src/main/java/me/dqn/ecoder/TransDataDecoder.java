package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.dqn.protocol.TransData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author dqn
 * created at 2019/3/24 18:39
 */
public class TransDataDecoder extends ByteToMessageDecoder {
    Logger logger = LoggerFactory.getLogger(TransDataDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        logger.info("client readable: {}",in.readableBytes());
        if (in == null) return;
        int size = in.readInt();
        int type = in.readInt();
        long sess = in.readLong();
        int fromPort = in.readInt();
        int toPort = in.readInt();
        byte[] data = new byte[size];
        in.readBytes(data);
        out.add(new TransData.Builder()
                .type(type)
                .sess(sess)
                .fromPort(fromPort)
                .toPort(toPort)
                .dataSize(size)
                .data(data)
                .build());
    }
}
