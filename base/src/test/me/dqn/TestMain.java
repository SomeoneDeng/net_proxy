package me.dqn;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

/**
 * @author dqn
 * created at 2019/4/28 0:08
 */
public class TestMain {
    private Logger logger = LoggerFactory.getLogger(TestMain.class);

    /**
     * 测试解码/编码器
     */
    @Test
    public void testConfigRead() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        LinkedHashMap map = yaml.load(new FileInputStream(new File("D:\\tech\\java\\net_proxy\\server\\src\\main\\resources\\server.yml")));
        if (map == null) {
            logger.info("读取配置文件失败");
        }
        assert map != null;
        map.forEach((k, v) -> {
            logger.info("key: {}, value: {}, value type: {}", k, v, v.getClass());
        });
    }



}
