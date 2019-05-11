package com.lzf.stackwatcher.collector.subscriber;

import com.lzf.stackwatcher.collector.DataSubscriber;
import com.lzf.stackwatcher.entity.Data;
import com.lzf.stackwatcher.entity.TimeSeriesData;

/**
 * 负责持久化的数据订阅者
 */
public abstract class PersistenceSubscriber implements DataSubscriber {

    @Override
    public boolean receiveType(Class<? extends Data> type) {
        return TimeSeriesData.class.isAssignableFrom(type);
    }
}
