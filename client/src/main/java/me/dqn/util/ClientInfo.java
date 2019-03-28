package me.dqn.util;

/**
 * @author dqn
 * created at 2019/3/27 1:52
 */
public class ClientInfo {
    private String clientName;
    private String HOST;
    private int PORT;
    // 代理的端口，届时放在配置文件
    private String serviceHost;
    private Integer fromPort;
    private Integer toPort;
    // tcp/http..
    private String type;

    public ClientInfo(String clientName, String HOST, int PORT, String serviceHost,
                      Integer fromPort, Integer toPort, String type) {
        this.clientName = clientName;
        this.HOST = HOST;
        this.PORT = PORT;
        this.serviceHost = serviceHost;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.type = type;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public Integer getFromPort() {
        return fromPort;
    }

    public void setFromPort(Integer fromPort) {
        this.fromPort = fromPort;
    }

    public Integer getToPort() {
        return toPort;
    }

    public void setToPort(Integer toPort) {
        this.toPort = toPort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
