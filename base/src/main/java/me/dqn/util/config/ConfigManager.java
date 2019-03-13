package me.dqn.util.config;

import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理配置文件
 *
 * @author dqn
 * created at 2019/3/13 4:15
 */
public class ConfigManager {
    private String configPath = "config.yml";
    /**
     * 配置信息
     */
    private Integer registerPort;


    public ConfigManager(String configPath) {
        ;
        this.configPath = configPath;
        readConfigFile();
    }

    private void readConfigFile() {
        Yaml yaml = new Yaml();
        LinkedHashMap map = yaml.load(this.getClass().getClassLoader().getResourceAsStream(configPath));
        registerPort = (Integer) map.get("registerPort");

    }

    public void setRegisterPort(Integer registerPort) {
        this.registerPort = registerPort;
    }

    public Integer getRegisterPort() {
        return registerPort;
    }
}