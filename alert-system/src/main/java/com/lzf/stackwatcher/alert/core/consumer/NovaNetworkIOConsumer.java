package com.lzf.stackwatcher.alert.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzf.stackwatcher.alert.core.Data;
import com.lzf.stackwatcher.alert.core.WarnRuleChecker;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.entity.TimeSeriesData;
import com.lzf.stackwatcher.entity.monitor.NovaNetworkIOMonitorData;

import java.util.List;
import java.util.Properties;

public class NovaNetworkIOConsumer extends Consumer {

    public NovaNetworkIOConsumer(WarnRuleChecker checker, String topic, Properties cfg) {
        super(checker, topic, cfg);
    }

    @Override
    protected void beforeRun() {
        log.info("物理机-网络IO监控数据告警处理器启动");
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
                NovaNetworkIOMonitorData data = new NovaNetworkIOMonitorData();
                JSONObject o = arr.getJSONObject(j);
                data.setTime(time)
                        .setHost(host);
                data.setDevice(o.getString("device"))
                        .setRxBytes(o.getLong("rxBytes"))
                        .setRxPackets(o.getLong("rxPackets"))
                        .setRxDrop(o.getLong("rxDrop"))
                        .setTxBytes(o.getLong("txPackets"))
                        .setTxPackets(o.getLong("txPackets"))
                        .setTxDrop(o.getLong("txDrop"))
                        .setRxByteSpeed(o.getInteger("rxByteSpeed"))
                        .setRxPacketSpeed(o.getInteger("rxPacketSpeed"))
                        .setTxByteSpeed(o.getInteger("txByteSpeed"))
                        .setTxPacketSpeed(o.getInteger("txPacketSpeed"));
                out.add(data);
            }
        }
    }

    @Override
    protected void resolveTimeSerialData(TimeSeriesData tsd, List<Data> out) {
        NovaNetworkIOMonitorData data = (NovaNetworkIOMonitorData) tsd;
        String host = data.getHost();
        long time = data.getTime();
        String device = data.getDevice();

        out.add(new Data(host, Rule.Type.NOVA_NET_RX_BYTES, device, data.getRxBytes(), time));
        out.add(new Data(host, Rule.Type.NOVA_NET_RX_PACKAGES, device, data.getRxPackets(), time));
        out.add(new Data(host, Rule.Type.NOVA_NET_RX_DROP, device, data.getRxDrop(), time));
        out.add(new Data(host, Rule.Type.NOVA_NET_TX_BYTES, device, data.getTxBytes(), time));
        out.add(new Data(host, Rule.Type.NOVA_NET_TX_PACKAGES, device, data.getTxPackets(), time));
        out.add(new Data(host, Rule.Type.NOVA_NET_TX_DROP, device, data.getTxDrop(), time));
    }
}
