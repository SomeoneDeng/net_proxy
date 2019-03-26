package me.dqn.protocol;

import java.io.Serializable;

/**
 * @author dqn
 * created at 2019/3/13 17:11
 */
public class TransData implements Serializable {
    private static final long serialVersionUID = -8076742702215522708L;
    // 注册
    public final static int TYPE_REG = 1;
    // 数据
    public final static int TYPE_DT = 2;
    // 心跳
    public final static int TYPE_HT = 3;
    // 断开连接
    public final static int TYPT_DIS = 4;
    // session id
    private long sess;
    // 真实端口
    private int fromPort;
    // 代理端口
    private int toPort;
    /**
     * info: 1, data: 2
     */
    private int type;

    private int dataSize;

    /**
     * if type 2
     */
    private byte[] data;

    public static class Builder {
        private TransData transData;

        public Builder() {
            transData = new TransData();
        }

        public Builder fromPort(int fromPort) {
            transData.setFromPort(fromPort);
            return this;
        }

        public Builder toPort(int toPort) {
            transData.setToPort(toPort);
            return this;
        }

        public Builder type(int type) {
            transData.setType(type);
            return this;
        }

        public Builder dataSize(int size) {
            transData.setDataSize(size);
            return this;
        }

        public Builder data(byte[] data) {
            transData.setData(data);
            return this;
        }

        public Builder sess(long sess) {
            transData.setSess(sess);
            return this;
        }

        public TransData build() {
            return this.transData;
        }

    }

    public long getSess() {
        return sess;
    }

    public void setSess(long sess) {
        this.sess = sess;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getFromPort() {
        return fromPort;
    }

    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }

    public int getToPort() {
        return toPort;
    }

    public void setToPort(int toPort) {
        this.toPort = toPort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
