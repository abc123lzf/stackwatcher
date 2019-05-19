package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Alert;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.InstanceAgentCPUMonitorData;

import java.util.List;
import java.util.Properties;

public class InstanceAgentCPUConsumer extends Consumer {

    public InstanceAgentCPUConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
    }

    @Override
    protected void beforeRun() {
        log.info("InstanceAgent-CPU监控数据告警判定处理器启动");
    }

    @Override
    protected void handlerJSONData(String json, List<TimeSeriesData> out) throws Exception {
        JSONArray arr = JSON.parseArray(json);
        for(int i = 0; i < arr.size(); i++) {
            JSONObject object = arr.getJSONObject(i);

            String uuid = object.getString("uuid");
            long time = object.getLong("time");
            String host = object.getString("host");

            InstanceAgentCPUMonitorData data = new InstanceAgentCPUMonitorData();

            data.setUuid(uuid)
                    .setTime(time)
                    .setHost(host);
            data.setIdle(object.getFloat("idle"))
                    .setSystem(object.getFloat("system"))
                    .setIowait(object.getFloat("iowait"))
                    .setUser(object.getFloat("user"))
                    .setOther(object.getFloat("other"))
                    .setTotalUsed(object.getFloat("totalused"));

            out.add(data);
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        InstanceAgentCPUMonitorData data = (InstanceAgentCPUMonitorData) tsd;
        String uuid = data.getUuid();
        long time = data.getTime();

        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_IDLE, data.getIdle(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_SYSTEM, data.getSystem(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_IOWAIT, data.getIowait(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_USER, data.getUser(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_OTHER, data.getOther(), time));
        out.add(new Data(uuid, Rule.Type.INS_AGENT_CPU_IDLE, data.getOther(), time));
    }
}
