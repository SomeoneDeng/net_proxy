package me.dqn.handler.status;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.dqn.interfaces.HttpHandle;
import me.dqn.server.channel.OuterChannelManager;
import me.dqn.util.StateInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dqn
 * created at 2019/5/3 3:55
 */
public class OuterHandler implements HttpHandle {
    @Override
    public void handler(ChannelHandlerContext context, HttpResponseStatus status) {

        List<StateInfo> collect = OuterChannelManager.outerChannelSpeed.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        Gson gson = new Gson();
        String s = gson.toJson(collect);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(s, CharsetUtil.UTF_8));
        response.headers().set("Access-Control-Allow-Headers", "access-control-allow-headers,access-control-allow-methods,access-control-allow-origin");
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("access-control-allow-methods", "GET,POST");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
