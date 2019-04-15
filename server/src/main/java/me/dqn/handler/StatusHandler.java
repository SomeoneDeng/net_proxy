package me.dqn.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于处理服务运行状态请求
 *
 * @author dqn
 * created at 2019/4/11 12:28
 */
public class StatusHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(StatusHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            logger.info("not a full http request,{}", msg.getClass());
        }
        FullHttpRequest request = (FullHttpRequest) msg;
        try {
            String uri = request.uri();
            if (!uri.equalsIgnoreCase("/test")) {
                handler(ctx, "错误的路径", HttpResponseStatus.FORBIDDEN);
            }
            if (!request.method().equals(HttpMethod.GET)) {
                handler(ctx, "错误的方法", HttpResponseStatus.FORBIDDEN);
            }
            handler(ctx, "{\"aa\":12313}", HttpResponseStatus.OK);
        } catch (Exception e) {
            logger.info("处理请求失败：{}", e.getMessage());
        } finally {
            request.release();
        }
    }

    private String getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    private void handler(ChannelHandlerContext context, String data, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
