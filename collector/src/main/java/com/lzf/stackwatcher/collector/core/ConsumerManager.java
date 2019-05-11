package com.lzf.stackwatcher.collector.core;

import com.lzf.stackwatcher.collector.Collector;
import com.lzf.stackwatcher.collector.consumer.*;
import com.lzf.stackwatcher.common.ContainerBase;
import com.lzf.stackwatcher.entity.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConsumerManager extends ContainerBase<Collector> {

    private final Collector collector;

    private final List<Consumer> consumers = new CopyOnWriteArrayList<>();

    private ExecutorService executor;

    public ConsumerManager(Collector collector) {
        setName("ConsumerManger");
        this.collector = collector;
    }

    @Override
    public Collector getParent() {
        return collector;
    }

    @Override
    protected void initInternal() {
        collector.registerConfig(new KafkaConfig(collector, collector.getZooKeeper()));
        executor = new ThreadPoolExecutor(14, 24, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(64));
    }

    @Override
    protected void startInternal() {
        KafkaConfig cfg = collector.getConfig(KafkaConfig.NAME, KafkaConfig.class);

        List<Consumer> list = new ArrayList<>(20);
        list.add(new InstanceCPUConsumer(cfg.getTopic(InstanceCPUConsumer.class), cfg.connectProperties()));
        list.add(new InstanceNetworkIOConsumer(cfg.getTopic(InstanceNetworkIOConsumer.class), cfg.connectProperties()));
        list.add(new InstanceDiskIOConsumer(cfg.getTopic(InstanceDiskIOConsumer.class), cfg.connectProperties()));
        list.add(new InstanceDiskCapacityConsumer(cfg.getTopic(InstanceDiskCapacityConsumer.class), cfg.connectProperties()));
        list.add(new NovaCPUConsumer(cfg.getTopic(NovaCPUConsumer.class), cfg.connectProperties()));
        list.add(new NovaMemoryConsumer(cfg.getTopic(NovaMemoryConsumer.class), cfg.connectProperties()));
        list.add(new NovaNetworkIOConsumer(cfg.getTopic(NovaNetworkIOConsumer.class), cfg.connectProperties()));
        list.add(new NovaDiskIOConsumer(cfg.getTopic(NovaDiskIOConsumer.class), cfg.connectProperties()));
        list.add(new NovaDiskCapacityConsumer(cfg.getTopic(NovaDiskCapacityConsumer.class), cfg.connectProperties()));
        list.add(new InstanceAgentCPUConsumer(cfg.getTopic(InstanceAgentCPUConsumer.class), cfg.connectProperties()));
        list.add(new InstanceAgentMemoryConsumer(cfg.getTopic(InstanceAgentMemoryConsumer.class), cfg.connectProperties()));
        list.add(new InstanceAgentNetworkIOConsumer(cfg.getTopic(InstanceAgentNetworkIOConsumer.class), cfg.connectProperties()));
        list.add(new InstanceAgentDiskConsumer(cfg.getTopic(InstanceAgentDiskConsumer.class), cfg.connectProperties()));

        consumers.addAll(list);

        for(Consumer c : consumers) {
            executor.submit(c);
        }

        executor.submit(new PublishTask());
    }

    private final class PublishTask implements Runnable {
        @Override
        public void run() {
            while(!Thread.interrupted()) {
                for (Consumer c : consumers) {
                    List<Data> data = c.publish();
                    if(data.size() > 0)
                        getParent().getSubscriberManager().notifySubscribers(data);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
