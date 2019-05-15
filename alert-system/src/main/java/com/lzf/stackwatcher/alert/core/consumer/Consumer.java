package com.lzf.stackwatcher.alert.core.consumer;

import com.lzf.stackwatcher.entity.TimeSeriesData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public abstract class Consumer implements Runnable {

    protected final Logger log;

    private final KafkaConsumer<String, String> kafka;


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
            ConsumerRecords<String, String> records = kafka.poll(Duration.ofMinutes(1));
            if(records == null)
                continue;

            for(ConsumerRecord<String, String> rec : records) {
                try {
                    handlerJSONData(rec.value(), dataList);
                    dataList.sort(Comparator.comparingLong(TimeSeriesData::getTime));

                    dataList.clear();
                } catch (Exception e) {
                    log.warn("处理JSON数据时发生异常", e);
                }
            }
        }

        kafka.close();
    }


    protected void checkData(String host, int type, double val, long time) {

    }


    protected void beforeRun() { }

    /**
     * 处理Agent发送的JSON格式的监控数据
     * @param json JSON数组字符串，数组每个元素对应一条监控数据
     */
    protected abstract void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception;

}
