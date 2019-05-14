package com.lzf.stackwatcher.sentinel.bean;

import java.util.List;

public class AgentNode {

    private String host;

    private List<String> instances;

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


}
