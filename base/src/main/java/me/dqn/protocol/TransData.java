package me.dqn.protocol;

/**
 * 数据包格式
 * 用于server和client之间传递数据
 *
 * @author dqn
 * created at 2019/3/12 3:40
 */
public class TransData {
    /**
     * 用户ip
     */
    private String ip;

    /**
     * 用户端口
     */
    private Integer uPort;

    /**
     * 服务器端口
     */
    private Integer sPort;

    /**
     * 会话id
     */
    private Long sessionID;

    /**
     * 真实服务器地址
     */
    private String remoteAddress;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 代理类型
     */
    private byte proxyType;

    /**
     * 优先级
     */
    private byte priority;

    /**
     * 命令
     */
    private byte[] command;

    /**
     * 数据
     */
    private byte[] data;

}
