package com.lzf.stackwatcher.entity.judge;

import com.lzf.stackwatcher.entity.Data;

import java.util.HashMap;
import java.util.Map;

public class WarnRule implements Data {
    private Integer id;
    //监控规则名
    private String name;
    //是否要求监控项全部满足
    private Term term;
    //监控数据周期, 单位秒
    private Integer period;
    //命中次数(检测结果连续符合多少次符合WarnPolicy就告警)
    private Integer frequency;
    //最大告警次数
    private Integer maxWarnTime;
    //报警级别
    private Level warnLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WarnRule() {}

    public WarnRule(String name, Term term, Integer period, Integer frequency,
                    Integer maxWarnTime, Level warnLevel) {
        this.name = name;
        this.term = term;
        this.period = period;
        this.frequency = frequency;
        this.maxWarnTime = maxWarnTime;
        this.warnLevel = warnLevel;
    }

    public enum Term {
        ALL(1, "all"), ONE(2, "one");
        private static final Map<String, Term> map = new HashMap<>(4);
        static {
            for (Term term : Term.values()) {
                map.put(term.value, term);
            }
        }

        public final int key;
        public final String value;

        private Term(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static Term getById(String key) {
            return map.get(key);
        }
    }

    public enum Level {
        INFO(1, "info"), WARN(2, "warn"), ERROR(3, "error"), FATAL(4, "fatal");
        private static final Map<String, Level> map = new HashMap<>(4);

        static {
            for (Level level : Level.values()) {
                map.put(level.value, level);
            }
        }

        public final int key;
        public final String value;

        private Level(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static Level getById(String key) {
            return map.get(key);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getMaxWarnTime() {
        return maxWarnTime;
    }

    public void setMaxWarnTime(Integer maxWarnTime) {
        this.maxWarnTime = maxWarnTime;
    }

    public Level getWarnLevel() {
        return warnLevel;
    }

    public void setWarnLevel(Level warnLevel) {
        this.warnLevel = warnLevel;
    }
}
