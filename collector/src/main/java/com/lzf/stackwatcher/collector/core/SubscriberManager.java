package com.lzf.stackwatcher.collector.core;

import com.lzf.stackwatcher.collector.Collector;
import com.lzf.stackwatcher.collector.DataSubscriber;
import com.lzf.stackwatcher.collector.subscriber.InfluxDBSubscriber;
import com.lzf.stackwatcher.common.ContainerBase;
import com.lzf.stackwatcher.entity.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriberManager extends ContainerBase<Collector> {

    private final Collector collector;

    private final List<DataSubscriber> subscribers = new CopyOnWriteArrayList<>();

    public SubscriberManager(Collector collector) {
        setName("SubscriberManager");
        this.collector = collector;
    }

    @Override
    public Collector getParent() {
        return collector;
    }

    @Override
    protected void initInternal() {
        InfluxDBSubscriber sub = new InfluxDBSubscriber(collector);
        subscribers.add(sub);
    }

    public void notifySubscribers(List<Data> list) {
        for(DataSubscriber sub : subscribers) {
            if(sub.receiveType(list.get(0).getClass()))
                sub.receive(list);
        }
    }

    public void notifySubscribers(Data data) {
        for(DataSubscriber sub : subscribers) {
            if(sub.receiveType(data.getClass()))
                sub.receive(data);
        }
    }
}
