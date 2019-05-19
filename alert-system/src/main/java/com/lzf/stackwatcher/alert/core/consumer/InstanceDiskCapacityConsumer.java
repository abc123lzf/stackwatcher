package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceDiskCapacityMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceDiskCapacityConsumer extends Consumer {

    public InstanceDiskCapacityConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
    }

    @Override
    protected void beforeRun() {
        log.info("Instance-存储容量监控数据告警处理器启动");
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);

            String uuid = object.getString("uuid");
            JSONArray arr = object.getJSONArray("data");
            long time = object.getLong("time");
            String host = object.getString("host");

            for (int j = 0; j < arr.size(); j++) {
                InstanceDiskCapacityMonitorData data = new InstanceDiskCapacityMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setUuid(uuid)
                        .setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setSize(o.getLong("capacity"))
                        .setUsage(o.getLong("allocation"));

                out.add(data);
            }
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        InstanceDiskCapacityMonitorData data = (InstanceDiskCapacityMonitorData) tsd;
        String uuid = data.getUuid();
        long time = data.getTime();
        String device = data.getDevice();

        out.add(new Data(uuid, Rule.Type.INS_DISK_USED, device, data.getUsage(), time));
        out.add(new Data(uuid, Rule.Type.INS_DISK_USED_UTILIZATION, device,
                data.getUsage().doubleValue() / data.getSize().doubleValue(), time));
    }
}
