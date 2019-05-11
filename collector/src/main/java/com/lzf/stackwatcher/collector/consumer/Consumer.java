package com.lzf.stackwatcher.collector.consumer;

import com.lzf.stackwatcher.entity.Data;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class Consumer implements Runnable {

    protected final Logger log;

    private final KafkaConsumer<String, String> kafka;

    private final BlockingQueue<Data> queue = new LinkedBlockingQueue<>();


    protected Consumer(String topic, Properties cfg) {
        this.log = LoggerFactory.getLogger(String.format("KafkaConsumer[topic:%s]",topic));
        this.kafka = new KafkaConsumer<>(Objects.requireNonNull(cfg));
        this.kafka.subscribe(Arrays.asList(Objects.requireNonNull(topic)));
    }

    @Override
    public final void run() {
        beforeRun();
        Thread t = Thread.currentThread();
        final List<TimeSeriesData> dataList = new ArrayList<>(256);
        while (!t.isInterrupted()) {
            ConsumerRecords<String, String> records = kafka.poll(Duration.ZERO);
            if(records == null)
                continue;
            for(TopicPartition tp : records.partitions()) {
                for(ConsumerRecord<String, String> rec : records.records(tp)) {
                    try {
                        handlerJSONData(rec.value(), dataList);
                        queue.addAll(dataList);
                        dataList.clear();
                    } catch (Exception e) {
                        log.warn("处理JSON数据时发生异常", e);
                    }
                }
            }
        }

        kafka.close();
    }

    public List<Data> publish() {
        List<Data> list = new ArrayList<>();
        Data data;
        try {
            while ((data = queue.poll(1, TimeUnit.MILLISECONDS)) != null) {
                list.add(data);
            }
            return list;
        } catch (InterruptedException e) {
            return list;
        }
    }


    protected void beforeRun() { }

    /**
     * 处理Agent发送的JSON格式的监控数据
     * @param json JSON数组字符串，数组每个元素对应一条监控数据
     */
    protected abstract void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception;

}
