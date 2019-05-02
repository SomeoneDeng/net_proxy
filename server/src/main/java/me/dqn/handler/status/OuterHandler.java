package me.dqn.handler.status;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.dqn.interfaces.HttpHandle;
import me.dqn.server.channel.OuterChannelManager;

/**
 * @author dqn
 * created at 2019/5/3 3:55
 */
public class OuterHandler implements HttpHandle {
    @Override
    public void handler(ChannelHandlerContext context, HttpResponseStatus status) {
        Gson gson = new Gson();
        String s = gson.toJson(OuterChannelManager.outerChannelSpeed);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(s, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
