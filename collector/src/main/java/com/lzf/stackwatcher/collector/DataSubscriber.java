package com.lzf.stackwatcher.collector;

import com.lzf.stackwatcher.entity.Data;

import java.util.List;

/**
 * 监控数据订阅者
 */
public interface DataSubscriber {

    /**
     * 接收监控数据
     * @param data 监控数据
     */
    void receive(Data data);

    /**
     * 同时接收多个数据
     * @param data List
     */
    void receive(List<Data> data);

    /**
     * 是否接受这个数据类型
     * @param type 数据类型
     * @return 是否接收
     */
    boolean receiveType(Class<? extends Data> type);
}
