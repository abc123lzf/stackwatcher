package com.lzf.stackwatcher.sentinel.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AgentNode extends NodeInfo {

    private String host;

    private boolean enableInstanceMonitor;

    private int instanceCPURate;

    private int instanceNetworkRate;

    private int instanceDiskIORate;

    private int instanceDiskCapacityRate;

    private int novaCPURate;

    private int novaMemoryRate;

    private int novaNetworkRate;

    private int novaDiskIORate;

    private int novaDiskCapacityRate;

    private boolean enableInstanceAgentMonitor;

    private int instanceAgentReceiverPort;

    private HostHardwareInfo hostHardwareInfo;

    private final List<InstanceHardwareInfo> instanceHardwareInfoList = new CopyOnWriteArrayList<>();

    private final List<StoragePoolInfo> storagePoolInfoList = new CopyOnWriteArrayList<>();

    private AgentNode() { }

    public static AgentNode parseZNodeString(byte[] content) throws Exception {
        if(content == null)
            return null;

        AgentNode node = new AgentNode();
        JSONObject obj = JSON.parseObject(new String(content, Charset.forName("UTF-8")));

        node.enableInstanceAgentMonitor = Boolean.valueOf(obj.getString("instance-monitor-enable"));

        JSONObject insRateObj = obj.getJSONObject("instance-monitor-rate");
        node.instanceCPURate = insRateObj.getIntValue("cpu");
        node.instanceNetworkRate = insRateObj.getIntValue("network");
        node.instanceDiskIORate = insRateObj.getIntValue("disk-io");
        node.instanceDiskCapacityRate = insRateObj.getIntValue("disk-capacity");

        JSONObject novaRateObj = obj.getJSONObject("nova-monitor-rate");
        node.novaCPURate = novaRateObj.getIntValue("cpu");
        node.novaMemoryRate = novaRateObj.getIntValue("memory");
        node.novaNetworkRate = novaRateObj.getIntValue("network");
        node.novaDiskIORate = novaRateObj.getIntValue("disk-io");
        node.novaDiskCapacityRate = novaRateObj.getIntValue("disk-capacity");

        node.instanceAgentReceiverPort = obj.getIntValue("instance-agent-port");
        node.enableInstanceAgentMonitor = node.instanceAgentReceiverPort > 0;

        HostHardwareInfo hostInfo = new HostHardwareInfo();
        JSONObject novaInfoObj = obj.getJSONObject("nova-info");
        hostInfo.hostName = novaInfoObj.getString("hostName");
        hostInfo.osName = novaInfoObj.getString("osName");
        hostInfo.osVersion = novaInfoObj.getString("osVersion");
        hostInfo.osArch = novaInfoObj.getString("osArch");
        hostInfo.localIp = novaInfoObj.getString("localIp");
        hostInfo.memory = novaInfoObj.getLongValue("memory");
        hostInfo.swapMemory = novaInfoObj.getLongValue("swapMemory");
        hostInfo.cpuVendor = novaInfoObj.getString("cpuVendor");
        hostInfo.cpuName = novaInfoObj.getString("cpuName");
        hostInfo.cpuMhz = novaInfoObj.getIntValue("cpuMhz");
        hostInfo.cpuNum = novaInfoObj.getIntValue("cpuNum");

        JSONArray diskArr = novaInfoObj.getJSONArray("diskInfo");
        HostHardwareInfo.NovaDiskInfo[] hdArr = new HostHardwareInfo.NovaDiskInfo[diskArr.size()];
        for(int i = 0; i < diskArr.size(); i++) {
            JSONObject t = diskArr.getJSONObject(i);
            hdArr[i] = new HostHardwareInfo.NovaDiskInfo(t.getString("name"), t.getString("path"),
                    t.getString("type"), t.getLongValue("size"));
        }
        hostInfo.diskInfo = hdArr;

        JSONArray netArr = novaInfoObj.getJSONArray("networkInfo");
        HostHardwareInfo.NovaNetworkInfo[] hnArr = new HostHardwareInfo.NovaNetworkInfo[netArr.size()];
        for(int i = 0; i < netArr.size(); i++) {
            JSONObject t = netArr.getJSONObject(i);
            hnArr[i] = new HostHardwareInfo.NovaNetworkInfo(t.getString("name"), t.getString("type"),
                    t.getString("ipAddr"), t.getString("netmask"), t.getString("broadCast"),
                    t.getString("macAddr"), t.getString("desc"));
        }
        hostInfo.networkInfo = hnArr;

        node.hostHardwareInfo = hostInfo;

        JSONArray insArr = obj.getJSONArray("instance-info");
        List<InstanceHardwareInfo> ihwl = new ArrayList<>(insArr.size());
        for(int i = 0; i < insArr.size(); i++) {
            JSONObject t = insArr.getJSONObject(i);
            InstanceHardwareInfo inf = new InstanceHardwareInfo();
            inf.host = t.getString("host");
            inf.id = t.getIntValue("id");
            inf.uuid = t.getString("uuid");
            inf.name = t.getString("name");
            inf.status = t.getIntValue("status");
            inf.vmType = t.getString("vmType");
            inf.osType = t.getString("osType");
            inf.memory = t.getLongValue("memory");
            inf.curMemory = t.getLongValue("curMemory");
            inf.vcpus = t.getIntValue("vcpus");

            JSONArray ia = t.getJSONArray("interfaceData");
            InstanceHardwareInfo.NetworkInterfaceInfo[] inarr = new InstanceHardwareInfo.NetworkInterfaceInfo[ia.size()];
            for(int j = 0; j < ia.size(); j++) {
                JSONObject iat = ia.getJSONObject(i);
                inarr[j] = new InstanceHardwareInfo.NetworkInterfaceInfo(iat.getString("macAddr"),
                        iat.getString("bridge"), iat.getString("netDevice"), iat.getString("source"));
            }
            inf.interfaceInfo = inarr;

            ia = t.getJSONArray("interfaceData");
            InstanceHardwareInfo.DiskInfo[] idarr = new InstanceHardwareInfo.DiskInfo[ia.size()];
            for(int j = 0; j < ia.size(); i++) {
                JSONObject iat = ia.getJSONObject(i);
                idarr[i] = new InstanceHardwareInfo.DiskInfo(iat.getString("type"), iat.getString("diskDevice"));
            }

            inf.diskInfo = idarr;
            ihwl.add(inf);
        }

        node.instanceHardwareInfoList.addAll(ihwl);

        JSONArray arr = obj.getJSONArray("storage-pool-info");
        List<StoragePoolInfo> pl = new ArrayList<>(arr.size());
        for(int i = 0; i < arr.size(); i++) {
            JSONObject t = arr.getJSONObject(i);
            pl.add(new StoragePoolInfo(t.getString("name"), t.getString("uuid"),
                    t.getLongValue("allocation"), t.getLongValue("available"),
                    t.getLongValue("capacity"), t.getString("status")));
        }

        node.storagePoolInfoList.addAll(pl);

        return node;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isEnableInstanceMonitor() {
        return enableInstanceMonitor;
    }

    public void setEnableInstanceMonitor(boolean enableInstanceMonitor) {
        this.enableInstanceMonitor = enableInstanceMonitor;
    }

    public int getInstanceCPURate() {
        return instanceCPURate;
    }

    public void setInstanceCPURate(int instanceCPURate) {
        this.instanceCPURate = instanceCPURate;
    }

    public int getInstanceNetworkRate() {
        return instanceNetworkRate;
    }

    public void setInstanceNetworkRate(int instanceNetworkRate) {
        this.instanceNetworkRate = instanceNetworkRate;
    }

    public int getInstanceDiskIORate() {
        return instanceDiskIORate;
    }

    public void setInstanceDiskIORate(int instanceDiskIORate) {
        this.instanceDiskIORate = instanceDiskIORate;
    }

    public int getInstanceDiskCapacityRate() {
        return instanceDiskCapacityRate;
    }

    public void setInstanceDiskCapacityRate(int instanceDiskCapacityRate) {
        this.instanceDiskCapacityRate = instanceDiskCapacityRate;
    }

    public int getNovaCPURate() {
        return novaCPURate;
    }

    public void setNovaCPURate(int novaCPURate) {
        this.novaCPURate = novaCPURate;
    }

    public int getNovaMemoryRate() {
        return novaMemoryRate;
    }

    public void setNovaMemoryRate(int novaMemoryRate) {
        this.novaMemoryRate = novaMemoryRate;
    }

    public int getNovaNetworkRate() {
        return novaNetworkRate;
    }

    public void setNovaNetworkRate(int novaNetworkRate) {
        this.novaNetworkRate = novaNetworkRate;
    }

    public int getNovaDiskIORate() {
        return novaDiskIORate;
    }

    public void setNovaDiskIORate(int novaDiskIORate) {
        this.novaDiskIORate = novaDiskIORate;
    }

    public int getNovaDiskCapacityRate() {
        return novaDiskCapacityRate;
    }

    public void setNovaDiskCapacityRate(int novaDiskCapacityRate) {
        this.novaDiskCapacityRate = novaDiskCapacityRate;
    }

    public boolean isEnableInstanceAgentMonitor() {
        return enableInstanceAgentMonitor;
    }

    public void setEnableInstanceAgentMonitor(boolean enableInstanceAgentMonitor) {
        this.enableInstanceAgentMonitor = enableInstanceAgentMonitor;
    }

    public int getInstanceAgentReceiverPort() {
        return instanceAgentReceiverPort;
    }

    public void setInstanceAgentReceiverPort(int instanceAgentReceiverPort) {
        this.instanceAgentReceiverPort = instanceAgentReceiverPort;
    }

    public HostHardwareInfo getHostHardwareInfo() {
        return hostHardwareInfo;
    }

    public void setHostHardwareInfo(HostHardwareInfo hostHardwareInfo) {
        this.hostHardwareInfo = hostHardwareInfo;
    }

    public List<InstanceHardwareInfo> getInstanceHardwareInfoList() {
        return instanceHardwareInfoList;
    }

    public List<StoragePoolInfo> getStoragePoolInfoList() {
        return storagePoolInfoList;
    }

    public void addInstanceHardwareInfoList(InstanceHardwareInfo info) {
        instanceHardwareInfoList.add(info);
    }

    public void addStoragePoolInfoList(StoragePoolInfo info) {
        storagePoolInfoList.add(info);
    }
}
