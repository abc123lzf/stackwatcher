package com.lzf.stackwatcher.alert.bootstrap;

import com.lzf.stackwatcher.alert.configuration.DruidDataSourceProperties;
import com.lzf.stackwatcher.alert.configuration.ZooKeeperConfig;
import com.lzf.stackwatcher.alert.core.AlertSystem;
import com.lzf.stackwatcher.common.ConfigManager;
import com.lzf.stackwatcher.common.DefaultConfigManager;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@MapperScan(value = {"com.lzf.stackwatcher.alert.dao"})
@ComponentScan(basePackages = {"com.lzf.stackwatcher.alert"})
@EnableConfigurationProperties(value = {DruidDataSourceProperties.class})
@EnableTransactionManagement
@EnableCaching
public class Bootstrap {

    private static final Logger log = LoggerFactory.getLogger("Bootstrap");

    public static void main(String[] args) throws Exception {
        log.info("Stackwatcher AlertSystem startup...");
        ConfigManager cm = new DefaultConfigManager("Temp-ConfigManager");
        ZooKeeperConfig cfg = new ZooKeeperConfig(cm);
        cm.registerConfig(cfg);

        ZooKeeper zooKeeper = new ZooKeeperConnector(cm);
        String path = "/stackwatcher/alert_system/" + cfg.getNodeName();

        log.info("Load zookeeper node {} to get config", path);

        byte[] b = zooKeeper.readNode(path);
        InputStream is = new ByteArrayInputStream(b);

        Properties p = new Properties();
        p.load(is);

        log.info("Startup spring boot application");
        SpringApplication app = new SpringApplicationBuilder(Bootstrap.class)
                .properties(p)
                .web(WebApplicationType.NONE).build();

        app.setDefaultProperties(p);
        ConfigurableApplicationContext ctx = app.run(args);

        log.info("Stackwatcher AlertSystem startup complete");

        zooKeeper.registerWatcher(path, e -> {
            log.info("Detect ZNode {} changed, begin to restart application...", path);
            ctx.close();
            zooKeeper.close();
            main(args);
        });
    }

    private final AlertSystem alertSystem;

    @Autowired
    public Bootstrap(AlertSystem alertSystem) {
        this.alertSystem = alertSystem;
    }

}
