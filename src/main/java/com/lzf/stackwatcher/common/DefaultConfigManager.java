package com.lzf.stackwatcher.common;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultConfigManager implements ConfigManager {

    private final String name;

    private final Map<String, Config> configMap = new ConcurrentHashMap<>();

    private final List<ConfigEventListener> listeners = new CopyOnWriteArrayList<>();

    public DefaultConfigManager(String name) {
        this.name = name;
        try {
            Class.forName("com.lzf.stackwatcher.url.ClasspathURLHandlerFactory");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void registerConfig(Config config) {
        String name;
        synchronized (configMap) {
            if (configMap.containsKey(name = config.getName()))
                throw new IllegalStateException(String.format("config %s is already exists", name));
            configMap.put(name, config);
        }

        try {
            config.initialize();
        } catch (ConfigInitializationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            synchronized (configMap) {
                configMap.remove(name);
                throw new RuntimeException(String.format("register config %s occur a exception", name), new ConfigInitializationException(e));
            }
        }

        fireConfigEvent(Config.REGISTER_EVENT, config);
    }

    @Override
    public void updateConfig(Config config) {
        String name;
        synchronized (configMap) {
            if (!configMap.containsKey(name = config.getName()))
                throw new IllegalStateException(String.format("config %s is not exists", name));

            configMap.put(name, config);
        }

        fireConfigEvent(Config.UPDATE_EVENT, config);
    }

    @Override
    public void removeConfig(Config config) {
        String name;
        synchronized (configMap) {
            if (!configMap.containsKey(name = config.getName()))
                throw new IllegalStateException(String.format("config %s is not exists", name));
            configMap.remove(name);
        }
        fireConfigEvent(Config.REMOVE_EVENT, config);
    }

    @Override
    public boolean saveConfig(Config config) {
        if(config.canSave()) {
            config.save();
            return true;
        }

        return false;
    }

    @Override
    public InputStream loadResource(String path) throws Exception {
        URL url = new URL(path);
        return url.openStream();
    }



    @Override
    public Config getConfig(String name) {
        return configMap.get(name);
    }

    @Override @SuppressWarnings("unchecked")
    public final  <T extends Config> T getConfig(String name, Class<T> requireType) {
        return (T) configMap.get(name);
    }

    @Override
    public void registerConfigEventListener(ConfigEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConfigEventListener(ConfigEventListener listener) {
        listeners.remove(listener);
    }


    protected void fireConfigEvent(ConfigEvent configEvent) {
        for(ConfigEventListener listener : listeners)
            listener.configEvent(configEvent);
    }

    private void fireConfigEvent(String type, Config config) {
        ConfigEvent event = new ConfigEvent(type, config, this);
        fireConfigEvent(event);
    }
}
