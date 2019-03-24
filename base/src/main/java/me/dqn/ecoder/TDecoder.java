package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.dqn.protocol.TransData;

import java.util.List;

/**
 * @author dqn
 * created at 2019/3/24 18:39
 */
public class TDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
