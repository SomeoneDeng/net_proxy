package me.dqn.protocol;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * @author dqn
 * created at 2019/3/13 17:11
 */
public class ClientInfo implements Serializable {


    private static final long serialVersionUID = -8076742702215522708L;
    /**
     * client 代理的端口
     */
    private Integer port;
    /**
     * 唯一标识
     */
    private Integer key;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
}
