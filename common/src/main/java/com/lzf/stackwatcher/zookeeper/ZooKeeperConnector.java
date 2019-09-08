package com.lzf.stackwatcher.zookeeper;

import com.lzf.stackwatcher.common.ConfigManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class ZooKeeperConnector implements ZooKeeper {

    private final ConfigManager configManager;

    private CuratorFramework zookeeper;

    private volatile boolean close = false;

    public ZooKeeperConnector(ConfigManager configManager) {
        this.configManager = configManager;
        ZooKeeper.Config config = configManager.getConfig(ZooKeeper.DEFAULT_CONFIG_NAME, ZooKeeper.Config.class);

        this.zookeeper = CuratorFrameworkFactory.newClient(config.getAddresses(), config.getSessionTimeout(),
                config.getConnectTimeout(), new ExponentialBackoffRetry(1000, 100, 60000));

        this.zookeeper.start();
    }
    @Override
    public void createEmptyNode(String path) throws Exception {
        zookeeper.create().forPath(path);
    }

    @Override
    public void createNode(String path, byte[] data) throws Exception {
        zookeeper.create().forPath(path, data);
    }

    @Override
    public void createTemporaryNode(String path, byte[] data) throws Exception {
        zookeeper.create().withMode(CreateMode.EPHEMERAL).forPath(path, data);
    }

    @Override
    public void createNodeRecursive(String path, byte[] data) throws Exception {
        zookeeper.create().creatingParentContainersIfNeeded().forPath(path, data);
    }

    @Override
    public void createTemporaryNodeRecursive(String path, byte[] data) throws Exception {
        zookeeper.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data);
    }

    @Override
    public void deleteNode(String path) throws Exception {
        zookeeper.delete().forPath(path);
    }

    @Override
    public void deleteNodeRecursive(String path) throws Exception {
        zookeeper.delete().deletingChildrenIfNeeded().forPath(path);
    }

    @Override
    public List<String> getChildNode(String path) throws Exception {
        return zookeeper.getChildren().forPath(path);
    }

    @Override
    public byte[] readNode(String path) throws Exception {
        return zookeeper.getData().forPath(path);
    }

    @Override
    public void updateNode(String path, byte[] data) throws Exception {
        zookeeper.setData().forPath(path, data);
    }

    @Override
    public void registerWatcher(String path, CuratorWatcher watcher) throws Exception {
        zookeeper.getData().usingWatcher(watcher).forPath(path);
    }

    @Override
    public NodeCache createNodeCache(String path) {
        return new NodeCache(zookeeper, path);
    }

    @Override
    public void close() {
        close = true;
        zookeeper.close();
    }
}
