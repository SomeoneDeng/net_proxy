package me.dqn.context;

import me.dqn.client.ClientMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

/**
 * client的配置信息，从client.yml中读取数据
 *
 * @author dqn
 * created at 2019/3/27 0:24
 */
public class ClientConfigure {
    Logger logger = LoggerFactory.getLogger(ClientConfigure.class);

    private List<ClientMeta> clients;
    private String serverHost;
    private int serverPort;

    public List<ClientMeta> getClients() {
        return clients;
    }

    public void setClients(List<ClientMeta> clients) {
        this.clients = clients;
    }

    private void loadConfigFile(String path) {
        logger.info("加载客户端配置信息...");
        Yaml yaml = new Yaml();


        Map<String, Object> configs = yaml.load(this.getClass().getClassLoader().getResourceAsStream(path));
        if (configs.size() <= 0) throw new RuntimeException("客户端配置为空");

        Map<String, Object> server = (Map<String, Object>) configs.get("server");
        serverHost = (String) server.get("host");
        serverPort = (int) server.get("port");
        List<Map<String, Object>> clientConfigs = (List<Map<String, Object>>) configs.get("clients");

        this.clients = new LinkedList<>();
        clients = clientConfigs.stream().map(map -> {
            String serviceHost = (String) map.get("host");
            String name = (String) map.get("client_name");
            String type = (String) map.get("type");
            int fromPort = (int) map.get("from_port");
            int toPort = (int) map.get("to_port");
            return new ClientMeta(
                    name,
                    serverHost,
                    serverPort,
                    serviceHost,
                    fromPort,
                    toPort,
                    type
            );
        }).collect(Collectors.toList());
        logger.info("加载了 {} 个服务..", clients.size());
    }

    public String getServerHost() {
        return serverHost;
    }


    public int getServerPort() {
        return serverPort;
    }

    public ClientConfigure(String configFilePath) {
        loadConfigFile(configFilePath);
    }
}
