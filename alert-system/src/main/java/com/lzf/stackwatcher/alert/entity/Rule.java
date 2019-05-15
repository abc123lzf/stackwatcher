package com.lzf.stackwatcher.alert.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Rule {
    private Integer id;

    private String name;

    private Short items;

    private Integer period;

    private Integer periodKeep;

    private String method;

    private String compare;

    private Double number;

    private Integer silenceTime;

    private Date startTime;

    private Date endTime;

    private Byte used;

    public enum Type {
        INS_CPU_USAGE(1, "ins.cpu.usage"),
        INS_NET_PUBLIC_IN(2, "ins.net.public.in"),
        INS_NET_PUBLIC_OUT(3, "ins.net.public.out"),
        INS_NET_PRIVATE_IN(4, "ins.net.private.in"),
        INS_NET_PRIVATE_OUT(5, "ins.net.private.out"),
        INS_DISK_READ_IOPS(6, "ins.disk.read.iops"),
        INS_DISK_READ_BYTES(7, "ins.disk.read.bytes"),
        INS_DISK_WRITE_IOPS(8, "ins.disk.write.iops"),
        INS_DISK_WRITE_BYTES(9, "ins.disk.write.bytes"),

        INS_AGENT_CPU_IDLE(10, "insagent.cpu.idle"),
        INS_AGENT_CPU_SYSTEM(11, "insagent.cpu.system"),
        INS_AGENT_CPU_IOWAIT(12, "insagent.cpu.iowait"),
        INS_AGENT_CPU_USER(13, "insagent.cpu.user"),
        INS_AGENT_CPU_OTHER(14, "insagent.cpu.other"),
        INS_AGENT_CPU_TOTAL(15, "insagent.cpu.total"),
        INS_AGENT_MEMORY_TOTAL(16, "insagent.memory.total"),
        INS_AGENT_MEMORY_USED(17, "insagent.memory.used"),
        INS_AGENT_MEMORY_ACTUALUSED(18, "insagent.memory.actualused"),
        INS_AGENT_MEMORY_FREE(19, "insagent.memory.free"),
        INS_AGENT_MEMORY_FREE_UTILIZATION(20, "insagent.memory.freeutilization"),
        INS_AGENT_MEMORY_USED_UTILIZATION(21, "insagent.memory.usedutilization"),
        INS_AGENT_DISK_USED(22, "insagent.disk.used"),
        INS_AGENT_DISK_UTILIZATION(23, "insagent.disk.utilization"),
        INS_AGENT_DISK_FREE(24, "insagent.disk.free"),
        INS_AGENT_DISK_TOTAL(25, "insagent.disk.total"),

        NOVA_CPU_USAGE(26, "nova.cpu.usage"),
        NOVA_MEMORY_TOTAL(27, "nova.memory.total"),
        NOVA_MEMORY_USED(28, "nova.memory.used"),
        NOVA_DISK_READ_IOPS(29, "nova.disk.read.iops"),
        NOVA_DISK_READ_BYTES(30, "nova.disk.read.bytes"),
        NOVA_DISK_WRITE_IOPS(31, "nova.disk.write.iops"),
        NOVA_DISK_WRITE_BYTES(32, "nova.disk.write.bytes"),
        NOVA_DISK_USED(33, "nova.disk.used"),
        NOVA_DISK_USED_UTILIZATION(34, "nova.disk.used.utilization");

        private static final Map<Short, Type> map = new HashMap<>(64);

        static {
            for(Type t : Type.values())
                map.put(t.key, t);
        }

        public final short key;
        public final String value;

        Type(int key, String value) {
            this.key = (short) key;
            this.value = value;
        }

        public static Type getById(int id) {
            return map.get((short)id);
        }
    }

    public enum Method {
        AVERAGE, ONLY, ALWAYS;
    }

    public enum Compare {
        LESS, EQUALS, GREATER;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Short getItems() {
        return items;
    }

    public void setItems(Short items) {
        this.items = items;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPeriodKeep() {
        return periodKeep;
    }

    public void setPeriodKeep(Integer periodKeep) {
        this.periodKeep = periodKeep;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public String getCompare() {
        return compare;
    }

    public Compare compare() {
        return Compare.valueOf(compare);
    }

    public Method method() {
        return Method.valueOf(method);
    }

    public Type type() {
        return Type.getById(items);
    }

    public void setCompare(String compare) {
        this.compare = compare == null ? null : compare.trim();
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public Integer getSilenceTime() {
        return silenceTime;
    }

    public void setSilenceTime(Integer silenceTime) {
        this.silenceTime = silenceTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getUsed() {
        return used;
    }

    public void setUsed(Byte used) {
        this.used = used;
    }
}