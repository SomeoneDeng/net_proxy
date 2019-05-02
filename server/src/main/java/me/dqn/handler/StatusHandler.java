package me.dqn.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import me.dqn.interfaces.HttpHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 外部请求的channel状态运行状态请求
 *
 * @author dqn
 * created at 2019/4/11 12:28
 */
public class StatusHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(StatusHandler.class);
    private Map<String, HttpHandle> handlers;

    public StatusHandler(Map<String, HttpHandle> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            logger.info("not a full http request,{}", msg.getClass());
        }
        FullHttpRequest request = (FullHttpRequest) msg;
        try {
            String uri = request.uri();
            if (!handlers.containsKey(uri)) {
                handlerError(ctx, "错误的路径", HttpResponseStatus.FORBIDDEN, msg);
            }
            HttpHandle httpHandle = handlers.get(uri);
            if (httpHandle != null) {
                httpHandle.handler(ctx, HttpResponseStatus.OK);
            }
        } catch (Exception e) {
            logger.info("处理请求失败：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlerError(ChannelHandlerContext context, String data, HttpResponseStatus status, Object msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        context.fireChannelRead(msg);
    }
}
