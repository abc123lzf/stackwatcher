package com.lzf.stackwatcher.entity.judge;

import com.lzf.stackwatcher.entity.Data;

import java.util.HashMap;
import java.util.Map;

public class WarnPolicy implements Data {

    private Integer ruleId;
    //监控类型
    private Type type;
    //监控方法
    private Method method;
    //比较符
    private CompareOperation operation;
    //目标数字
    private Double number;

    public WarnPolicy() { }

    public WarnPolicy(Integer ruleId, Type type, Method method, CompareOperation operation, Double number) {
        this.ruleId = ruleId;
        this.type = type;
        this.method = method;
        this.operation = operation;
        this.number = number;
    }

    @Override
    public String toString() {
        return "WarnPolicy{" +
                "ruleId=" + ruleId +
                ", type=" + type +
                ", method=" + method +
                ", operation=" + operation +
                ", number=" + number +
                '}';
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public CompareOperation getOperation() {
        return operation;
    }

    public void setOperation(CompareOperation operation) {
        this.operation = operation;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public enum Type {
        INS_CPU_USAGE(1, "ins.cpu.usage", Float.class),
        INS_NET_PUBLIC_IN(2, "ins.net.public.in", Integer.class),
        INS_NET_PUBLIC_OUT(3, "ins.net.public.out", Integer.class),
        INS_NET_PRIVATE_IN(4, "ins.net.private.in", Integer.class),
        INS_NET_PRIVATE_OUT(5, "ins.net.private.out", Integer.class),
        INS_DISK_READ_IOPS(6, "ins.disk.read.iops", Integer.class),
        INS_DISK_READ_BYTES(7, "ins.disk.read.bytes", Integer.class),
        INS_DISK_WRITE_IOPS(8, "ins.disk.write.iops", Integer.class),
        INS_DISK_WRITE_BYTES(9, "ins.disk.write.bytes", Integer.class),

        INS_AGENT_CPU_IDLE(10, "insagent.cpu.idle", Float.class),
        INS_AGENT_CPU_SYSTEM(11, "insagent.cpu.system", Float.class),
        INS_AGENT_CPU_IOWAIT(12, "insagent.cpu.iowait", Float.class),
        INS_AGENT_CPU_USER(13, "insagent.cpu.user", Float.class),
        INS_AGENT_CPU_OTHER(14, "insagent.cpu.other", Float.class),
        INS_AGENT_CPU_TOTAL(15, "insagent.cpu.total", Float.class),
        INS_AGENT_MEMORY_TOTAL(16, "insagent.memory.total", Long.class),
        INS_AGENT_MEMORY_USED(17, "insagent.memory.used", Long.class),
        INS_AGENT_MEMORY_ACTUALUSED(18, "insagent.memory.actualused", Long.class),
        INS_AGENT_MEMORY_FREE(19, "insagent.memory.free", Long.class),
        INS_AGENT_MEMORY_FREE_UTILIZATION(20, "insagent.memory.freeutilization", Float.class),
        INS_AGENT_MEMORY_USED_UTILIZATION(21, "insagent.memory.usedutilization", Float.class),
        INS_AGENT_DISK_USED(22, "insagent.disk.used", Long.class),
        INS_AGENT_DISK_UTILIZATION(23, "insagent.disk.utilization", Float.class),
        INS_AGENT_DISK_FREE(24, "insagent.disk.free", Long.class),
        INS_AGENT_DISK_TOTAL(25, "insagent.disk.total", Long.class),

        NOVA_CPU_USAGE(26, "nova.cpu.usage", Float.class),
        NOVA_MEMORY_TOTAL(27, "nova.memory.total", Long.class),
        NOVA_MEMORY_USED(28, "nova.memory.used", Long.class),
        NOVA_DISK_READ_IOPS(29, "nova.disk.read.iops", Integer.class),
        NOVA_DISK_READ_BYTES(30, "nova.disk.read.bytes", Integer.class),
        NOVA_DISK_WRITE_IOPS(31, "nova.disk.write.iops", Integer.class),
        NOVA_DISK_WRITE_BYTES(32, "nova.disk.write.bytes", Integer.class),
        NOVA_DISK_USED(33, "nova.disk.used", Long.class),
        NOVA_DISK_USED_UTILIZATION(34, "nova.disk.used.utilization", Long.class);

        private static final Map<String, Type> map = new HashMap<>(64);

        static {
            for(Type t : Type.values())
                map.put(t.value, t);
        }

        public final int key;
        public final String value;
        public final Class<? extends Number> dataType;

        private Type(int key, String value, Class<? extends Number> dataType) {
            this.key = key;
            this.value = value;
            this.dataType = dataType;
        }

        public static Type getById(String id) {
            return map.get(id);
        }
    }

    public enum Method {
        MAX(1, "max"), AVERAGE(2, "avg"), MIN(3, "min");
        private static final Map<String, Method> map = new HashMap<>();
        static {
            for(Method m : Method.values())
                map.put(m.value, m);
        }

        public final int key;
        public final String value;

        private Method(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static Method getById(String id) {
            return map.get(id);
        }
    }

    public enum CompareOperation {
        MORE(1, ">="), EQUAL(2, "="), LESS(3, "<=");
        private static final Map<String, CompareOperation> map = new HashMap<>();

        static {
            for(CompareOperation co : CompareOperation.values())
                map.put(co.value, co);
        }

        public final int key;
        public final String value;

        private CompareOperation(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static CompareOperation getById(String id) {
            return map.get(id);
        }

        public boolean compare(float source, double target) {
            switch (this) {
                case LESS: return source <= target;
                case EQUAL: return source == target;
                case MORE: return source >= target;
            }
            throw new Error();
        }

        public boolean compare(int source, double target) {
            switch (this) {
                case LESS: return source <= target;
                case EQUAL: return source == target;
                case MORE: return source >= target;
            }
            throw new Error();
        }

        public boolean compare(long source, double target) {
            switch (this) {
                case LESS: return source <= target;
                case EQUAL: return source == target;
                case MORE: return source >= target;
            }
            throw new Error();
        }

        public boolean compare(double source, double target) {
            switch (this) {
                case LESS: return source <= target;
                case EQUAL: return source == target;
                case MORE: return source >= target;
            }
            throw new Error();
        }

        public boolean compare(Number source, double target) {
            if (source instanceof Integer) {
                return compare((int) source, target);
            } else if (source instanceof Long) {
                return compare((long) source, target);
            } else if (source instanceof Float) {
                return compare((float) source, target);
            } else if (source instanceof Double) {
                return compare((double) source, target);
            }

            return compare((double) source, target);
        }
    }
}
