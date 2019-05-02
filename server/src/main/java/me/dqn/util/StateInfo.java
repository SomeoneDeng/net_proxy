package me.dqn.util;

/**
 * @author dqn
 * created at 2019/5/3 4:18
 */
public class StateInfo {
    private String from;
    private Long readSpeed;
    private Long writeSpeed;
    private String channelId;

    public StateInfo() {
    }

    public StateInfo(String from, Long readSpeed, Long writeSpeed, String channelId) {
        this.from = from;
        this.readSpeed = readSpeed;
        this.writeSpeed = writeSpeed;
        this.channelId = channelId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getReadSpeed() {
        return readSpeed;
    }

    public void setReadSpeed(Long readSpeed) {
        this.readSpeed = readSpeed;
    }

    public Long getWriteSpeed() {
        return writeSpeed;
    }

    public void setWriteSpeed(Long writeSpeed) {
        this.writeSpeed = writeSpeed;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
