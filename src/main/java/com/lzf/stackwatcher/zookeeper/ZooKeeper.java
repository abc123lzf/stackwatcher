package com.lzf.stackwatcher.zookeeper;


import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;

import java.util.List;


public interface ZooKeeper {

    String DEFAULT_CONFIG_NAME = "config.zookeeper";

    void createEmptyNode(String path) throws Exception;

    void createNode(String path, byte[] data) throws Exception;

    void createTemporaryNode(String path, byte[] data) throws Exception;

    void createNodeRecursive(String path, byte[] data) throws Exception;

    void createTemporaryNodeRecursive(String path, byte[] data) throws Exception;

    void deleteNode(String path) throws Exception;

    void deleteNodeRecursive(String path) throws Exception;

    byte[] readNode(String path) throws Exception;

    void updateNode(String path, byte[] data) throws Exception;

    void registerWatcher(String path, CuratorWatcher watcher) throws Exception;

    NodeCache createNodeCache(String path);

    List<String> getChildNode(String path) throws Exception;

    void close();

    interface Config extends com.lzf.stackwatcher.common.Config {

        String getAddresses();

        int getConnectTimeout();

        int getSessionTimeout();
    }
}
