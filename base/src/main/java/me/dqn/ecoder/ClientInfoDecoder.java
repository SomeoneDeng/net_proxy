package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.dqn.protocol.TransData;

/**
 * @author dqn
 * created at 2019/3/13 19:47
 */
public class ClientInfoDecoder extends LengthFieldBasedFrameDecoder {


    public ClientInfoDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        TransData transData = new TransData();

        int type = in.readInt();
        long sess = in.readLong();
        int fromPort = in.readInt();
        int toPort = in.readInt();
        int size = in.readInt();
        byte[] bytes = null;
        if (type == TransData.TYPE_DT) {
            int len = in.readableBytes();
            // TODO: 2019/3/22 大小校验
            bytes = new byte[size];
            in.readBytes(bytes);
        }

        transData.setType(type);
        transData.setSess(sess);
        transData.setFromPort(fromPort);
        transData.setToPort(toPort);
        transData.setDataSize(size);
        transData.setData(bytes);
        return transData;
    }
}
