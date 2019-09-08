package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.configuration.AlertMessageConfig;
import com.lzf.stackwatcher.alert.configuration.KafkaConfig;
import com.lzf.stackwatcher.alert.configuration.ZooKeeperConfig;
import com.lzf.stackwatcher.alert.core.consumer.*;
import com.lzf.stackwatcher.common.*;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import com.lzf.stackwatcher.zookeeper.ZooKeeperConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;

@Component
public class AlertSystem extends ContainerBase<Void> implements ConfigManager, DisposableBean, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger("AlertSystem");

    private final ConfigManager configManager = new DefaultConfigManager("ConfigManager");

    private WarnRuleChecker ruleChecker;

    private ZooKeeper zooKeeper;

    private ExecutorService executor;

    @Value("${stackwatcher.consumer.device.public-network-inteface}")
    private String instancePublicInterface;

    @Value("${stackwatcher.consumer.device.private-network-interface}")
    private String instancePrivateInterface;


    public AlertSystem() {
        log.info("Begin object AlertSystem init...");
        setName("AlertSystem");
        init();
        log.info("AlertSystem initialize complete");
    }

    @Autowired
    public void setWarnRuleChecker(WarnRuleChecker ruleChecker) {
        this.ruleChecker = ruleChecker;
    }

    @Override
    public void afterPropertiesSet() {
        start();
    }

    @Override
    protected void initInternal() {
        executor = new ThreadPoolExecutor(14, 24, 500, TimeUnit.SECONDS, new ArrayBlockingQueue<>(24));
        configManager.registerConfig(new ZooKeeperConfig(configManager));
        configManager.registerConfig(new AlertMessageConfig(configManager));
        log.info("Connect to zookeeper server...");
        zooKeeper = new ZooKeeperConnector(configManager);
        log.info("Zookeeper connection established");
    }

    @Override
    protected void startInternal() {
        log.info("Starting AlertSystem");
        KafkaConfig config = new KafkaConfig(configManager, zooKeeper);
        configManager.registerConfig(config);

        executor.submit(new InstanceAgentCPUConsumer(ruleChecker, config.getTopic(InstanceAgentCPUConsumer.class), config.connectProperties()));
        executor.submit(new InstanceAgentMemoryConsumer(ruleChecker, config.getTopic(InstanceAgentMemoryConsumer.class), config.connectProperties()));
        executor.submit(new InstanceAgentDiskConsumer(ruleChecker, config.getTopic(InstanceAgentDiskConsumer.class), config.connectProperties()));
        executor.submit(new InstanceCPUConsumer(ruleChecker, config.getTopic(InstanceCPUConsumer.class), config.connectProperties()));

        Properties p = config.connectProperties();
        p.setProperty("instance.public-interface-name", instancePublicInterface);
        p.setProperty("instance.private-interface-name", instancePrivateInterface);
        executor.submit(new InstanceNetworkIOConsumer(ruleChecker, config.getTopic(InstanceNetworkIOConsumer.class), p));

        executor.submit(new InstanceDiskIOConsumer(ruleChecker, config.getTopic(InstanceDiskIOConsumer.class), config.connectProperties()));
        executor.submit(new InstanceDiskCapacityConsumer(ruleChecker, config.getTopic(InstanceDiskCapacityConsumer.class), config.connectProperties()));

        executor.submit(new NovaCPUConsumer(ruleChecker, config.getTopic(NovaCPUConsumer.class), config.connectProperties()));
        executor.submit(new NovaMemoryConsumer(ruleChecker, config.getTopic(NovaMemoryConsumer.class), config.connectProperties()));
        executor.submit(new NovaDiskIOConsumer(ruleChecker, config.getTopic(NovaDiskIOConsumer.class), config.connectProperties()));
        executor.submit(new NovaDiskCapacityConsumer(ruleChecker, config.getTopic(NovaDiskCapacityConsumer.class), config.connectProperties()));

        log.info("Start AlertSystem complete");
    }

    @Override
    protected void stopInternal() {
        zooKeeper.close();
        executor.shutdownNow();
    }

    @Override
    public void destroy() {
        stop();
    }

    @Bean
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    @Override
    public void registerConfig(Config config) {
        configManager.registerConfig(config);
    }

    @Override
    public void updateConfig(Config config) {
        configManager.updateConfig(config);
    }

    @Override
    public void removeConfig(Config config) {
        configManager.removeConfig(config);
    }

    @Override
    public boolean saveConfig(Config config) {
        return configManager.saveConfig(config);
    }

    @Override
    public Config getConfig(String name) {
        return configManager.getConfig(name);
    }

    @Override
    public <C extends Config> C getConfig(String name, Class<C> requireType) {
        return configManager.getConfig(name, requireType);
    }

    @Override
    public void registerConfigEventListener(ConfigEventListener listener) {
        configManager.registerConfigEventListener(listener);
    }

    @Override
    public void removeConfigEventListener(ConfigEventListener listener) {
        configManager.removeConfigEventListener(listener);
    }

    @Override
    public InputStream loadResource(String path) throws Exception {
        return configManager.loadResource(path);
    }
}
