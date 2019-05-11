package com.lzf.stackwatcher.common;

import java.util.EventListener;

@FunctionalInterface
public interface ConfigEventListener extends EventListener {

    void configEvent(ConfigEvent configEvent);

}
