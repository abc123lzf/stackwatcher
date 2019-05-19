package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaDiskIOMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaDiskIOConsumer extends Consumer {

    public NovaDiskIOConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
    }

    @Override
    protected void beforeRun() {
        log.info("物理机-磁盘IO监控数据告警处理器启动");
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr0 = JSON.parseArray(json);
        for(int i = 0; i < arr0.size(); i++) {
            JSONObject object = arr0.getJSONObject(i);

            String host = object.getString("host");
            JSONArray arr = object.getJSONArray("data");
            long time = object.getLong("time");

            for (int j = 0; j < arr.size(); j++) {
                NovaDiskIOMonitorData data = new NovaDiskIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRdBytes(o.getInteger("reBytes"))
                        .setRdReq(o.getInteger("read"))
                        .setWrBytes(o.getInteger("wrBytes"))
                        .setWrReq(o.getInteger("write"));
                out.add(data);
            }
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        NovaDiskIOMonitorData data = (NovaDiskIOMonitorData) tsd;
        String host = data.getHost();
        long time = data.getTime();
        String device = data.getDevice();

        out.add(new Data(host, Rule.Type.NOVA_DISK_READ_BYTES, device, data.getRdBytes(), time));
        out.add(new Data(host, Rule.Type.NOVA_DISK_READ_IOPS, device, data.getRdReq(), time));
        out.add(new Data(host, Rule.Type.NOVA_DISK_WRITE_BYTES, device, data.getWrBytes(), time));
        out.add(new Data(host, Rule.Type.NOVA_DISK_WRITE_IOPS, device, data.getWrReq(), time));
    }
}
