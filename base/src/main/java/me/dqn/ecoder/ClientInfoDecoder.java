package me.dqn.ecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.dqn.protocol.ClientInfo;

import java.nio.ByteOrder;

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
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setKey(in.readInt());
        clientInfo.setPort(in.readInt());
        return clientInfo;
    }
}
