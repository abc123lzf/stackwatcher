package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaCPUMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaCPUConsumer extends Consumer {

    public NovaCPUConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
    }

    @Override
    protected void beforeRun() {
        log.info("物理机-CPU监控数据告警处理器启动");
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            NovaCPUMonitorData data = new NovaCPUMonitorData();
            JSONObject object = arr.getJSONObject(i);
            String host = object.getString("host");

            data.setTotal(object.getFloat("total"))
                    .setSystem(object.getFloat("system"))
                    .setUser(object.getFloat("user"))
                    .setIowait(object.getFloat("iowait"))
                    .setOther(object.getFloat("other"))
                    .setTime(object.getLong("time"))
                    .setHost(host);

            out.add(data);
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        NovaCPUMonitorData data = (NovaCPUMonitorData) tsd;
        String host = data.getHost();
        long time = data.getTime();

        out.add(new Data(host, Rule.Type.NOVA_CPU_USAGE, data.getTotal(), time));
        out.add(new Data(host, Rule.Type.NOVA_CPU_SYSTEM, data.getSystem(), time));
        out.add(new Data(host, Rule.Type.NOVA_CPU_IOWAIT, data.getIowait(), time));
        out.add(new Data(host, Rule.Type.NOVA_CPU_USER, data.getUser(), time));
        out.add(new Data(host, Rule.Type.NOVA_CPU_OTHER, data.getOther(), time));
    }
}
