package com.lzf.stackwatcher.alert.core.consumer;

import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
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

    private final WarnRuleChecker ruleChecker;

    protected Consumer(WarnRuleChecker checker, String topic, Properties cfg) {
        this.log = LoggerFactory.getLogger(String.format("KafkaConsumer[topic:%s]",topic));
        this.kafka = new KafkaConsumer<>(Objects.requireNonNull(cfg));
        this.kafka.subscribe(Arrays.asList(Objects.requireNonNull(topic)));
        this.ruleChecker = checker;
    }

    @Override
    public final void run() {
        beforeRun();
        Thread t = Thread.currentThread();
        final List<TimeSeriesData> tsdList = new ArrayList<>(256);
        final List<Data> dList = new ArrayList<>(1024);
        while (!t.isInterrupted()) {
            ConsumerRecords<String, String> records = kafka.poll(Duration.ofMinutes(1));
            if(records == null)
                continue;

            for(ConsumerRecord<String, String> rec : records) {
                try {
                    handlerJSONData(rec.value(), tsdList);
                    //按照数据的采集时间排序
                    tsdList.sort(Comparator.comparingLong(TimeSeriesData::getTime));
                    for(TimeSeriesData data : tsdList) {
                        resolveTimeSerialData(data, dList);
                    }

                    for(Data data : dList) {
                        checkData(data);
                    }

                    dList.clear();
                    tsdList.clear();
                } catch (Exception e) {
                    log.warn("处理JSON数据时发生异常", e);
                }
            }
        }

        kafka.close();
    }

    /**
     * 将TimeSeriesData转换为Data
     * @param tsd 时序数据
     * @param out 数据
     */
    protected abstract void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out);

    private void checkData(Data data) {
        ruleChecker.checkData(data);
    }


    protected void beforeRun() { }

    /**
     * 处理Agent发送的JSON格式的监控数据
     * @param json JSON数组字符串，数组每个元素对应一条监控数据
     */
    protected abstract void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception;

}
