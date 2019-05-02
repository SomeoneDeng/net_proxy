package me.dqn.interfaces;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author dqn
 * created at 2019/5/3 3:45
 */
public interface HttpHandle {
    public void handler(ChannelHandlerContext context, HttpResponseStatus status);
}
