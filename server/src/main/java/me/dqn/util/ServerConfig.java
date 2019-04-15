package me.dqn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dqn
 * created at 2019/3/14 1:09
 */
public class ServerConfig {
    private Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    private String configPath;
    /**
     * 端口映射表
     * 根据这个可以确定 接收端口，转发给客户端的的端口，以及对应关系
     */
    public static Map<Integer, Integer> portMapping;

    /**
     * 注册端口
     */
    private Integer registerPort;
    private Integer statusPort;
    private Integer heartBeatTime;

    public ServerConfig(String configPath) {
        this.configPath = configPath;
        portMapping = new ConcurrentHashMap<>(16);
        readConfigFile();
    }

    /**
     * 从客户端加载配置文件，初始化服务端
     */
    private void readConfigFile() {
        logger.info("加载客户端配置");
        Yaml yaml = new Yaml();
        LinkedHashMap map = yaml.load(this.getClass().getClassLoader().getResourceAsStream(configPath));
        registerPort = (Integer) map.get("registerPort");
        statusPort = (Integer) map.get("statusPort");
        heartBeatTime = (Integer) map.get("heartbeat_time");
        List<LinkedHashMap<String, Object>> clients = (List<LinkedHashMap<String, Object>>) map.get("clients");
        clients.forEach(client -> {
            logger.info("客户端：{}", client);
            portMapping.put((Integer) client.get("realPort"), (Integer) client.get("port"));
        });
    }

    public void setRegisterPort(Integer registerPort) {
        this.registerPort = registerPort;
    }

    public Integer getRegisterPort() {
        return registerPort;
    }

    public Integer getHeartBeatTime() {
        return heartBeatTime;
    }

    public Integer getStatusPort() {
        return statusPort;
    }
}
