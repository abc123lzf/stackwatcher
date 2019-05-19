package com.lzf.stackwatcher.alert.core;

import com.lzf.stackwatcher.alert.dao.AlertMapper;
import com.lzf.stackwatcher.alert.dao.RuleMapper;
import com.lzf.stackwatcher.alert.entity.Rule;
import com.lzf.stackwatcher.alert.entity.RuleExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
public class WarnRuleChecker {
    private static final Logger log = LoggerFactory.getLogger(WarnRuleChecker.class);

    private final AlertMessageSender messageSender;

    private final RedisTemplate<String, String> ssRedis;
    private final RedisTemplate<String, Number> siRedis;

    private final RuleMapper ruleMapper;
    private final AlertMapper alertMapper;

    private final ExecutorService checkDataExecutor = new ThreadPoolExecutor(1, 1, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Autowired @SuppressWarnings("unchecked")
    public WarnRuleChecker(AlertMessageSender messageSender, RedisTemplate redisTemplate,
                           RuleMapper ruleMapper, AlertMapper alertMapper) {
        this.messageSender = messageSender;
        this.ssRedis = (RedisTemplate<String, String>) redisTemplate;
        this.siRedis = (RedisTemplate<String, Number>) redisTemplate;
        this.ruleMapper = ruleMapper;
        this.alertMapper = alertMapper;
    }


    private final class CheckDataCallback implements Runnable {
        private final Rule rule;
        private final Data data;

        public CheckDataCallback(Rule rule, Data data) {
            this.rule = rule;
            this.data = data;
        }

        @Override
        public void run() {
            checkDate0(rule, data);
        }
    }

    public void checkData(Data data) {
        List<Integer> ids = alertMapper.selectUsingRuleIdByObject(data.getHost());
        RuleExample re = new RuleExample();
        re.createCriteria().andIdIn(ids);

        List<Rule> list = ruleMapper.selectByExample(re);

        for(Rule rule : list) {
            if(rule.getUsed() == 0)
                continue;

            if(rule.getItems() != data.getType())
                continue;
            // TODO: 过滤时间
            checkDate0(rule, data);
        }
    }

    private void checkDate0(Rule rule, Data data) {
        final String host = data.getHost();
        final String device = data.getDevice();
        final double newValue = data.getValue();
        final long dataTime = data.getTime();

        final String key;
        if(device == null)
            key = host + "_" + rule.getId();
        else
            key = host + "_" + device + "_" + rule.getId();

        if(!lock(key)) { //如果没有加锁成功，那么将此任务包装为回调
            checkDataExecutor.submit(new CheckDataCallback(rule, data));
            return;
        }

        final double limit = rule.getNumber();

        Boolean hasKey = ssRedis.hasKey(key);
        try {
            if(hasKey != null && hasKey) {
                BoundHashOperations<String, String, Number> oper = siRedis.boundHashOps(key);
                long time = oper.get("st_time").longValue();
                double val = oper.get("val").doubleValue();

                switch (rule.method()) {
                    case ONLY: {
                        switch (rule.compare()) {
                            case GREATER:
                                if(val >= limit) {
                                    oper.put("statisfy", 1);
                                }
                                break;
                            case LESS:
                                if(val <= limit) {
                                    oper.put("statisfy", 1);
                                }
                        }
                        oper.put("val", newValue);
                    } break;

                    case ALWAYS: {
                        switch (rule.compare()) {
                            case GREATER: {
                                if(val >= limit) {
                                    oper.put("statisfy", 1);
                                } else {
                                    oper.put("statisfy", 0);
                                }
                            } break;
                            case LESS: {
                                if(val <= limit) {
                                    oper.put("statisfy", 1);
                                } else {
                                    oper.put("statisfy", 0);
                                }
                            }
                        }

                        oper.put("val", newValue);
                    } break;

                    case AVERAGE: {
                        val = (val + newValue) / 2.0;
                        switch (rule.compare()) {
                            case GREATER:
                                if(val >= limit) {
                                    oper.put("statisfy", 1);
                                } else {
                                    oper.put("statisfy", 0);
                                }
                                break;
                            case LESS:
                                if(val <= limit) {
                                    oper.put("statisfy", 1);
                                } else {
                                    oper.put("statisfy", 0);
                                }
                        }

                        oper.put("val", val);
                    } break;
                }

                //如果本周期结束
                if((dataTime - time) / 1000 > rule.getPeriod()) {
                    if(oper.get("statisfy").intValue() == 1) {
                        if (rule.getPeriodKeep() == 1 && setSilenceKey(key, rule.getSilenceTime())) {
                            messageSender.sendMessage(rule, data);
                        } else {
                            int t = setPeriodKey(key, rule.getPeriod(), rule.getPeriodKeep());
                            if(t >= rule.getPeriodKeep() && setSilenceKey(key, rule.getSilenceTime())) {
                                messageSender.sendMessage(rule, data);
                                deletePeriodKey(key);
                            }
                        }
                    }

                    siRedis.delete(key);
                }

                oper.put("val", val);

            } else {
                BoundHashOperations<String, String, Number> oper = siRedis.boundHashOps(key);
                oper.put("st_time", System.currentTimeMillis());
                oper.put("val", newValue);
                oper.put("statisfy", 0);

                switch (rule.compare()) {
                    case GREATER: {
                        if(newValue >= limit)
                            oper.put("statisfy", 1);
                        else
                            oper.put("statisfy", 0);
                    } break;

                    case LESS: {
                        if(newValue <= limit)
                            oper.put("statisfy", 1);
                        else
                            oper.put("statisfy", 0);
                    }
                }

                oper.expire(rule.getPeriod() * 2, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            log.warn("处理告警规则时发生异常", e);
        } finally {
            unlock(key);
        }
    }

    /**
     * 对指定键加上悲观锁(互斥锁)
     * 为了防止连接故障，该锁过期时间为10秒
     * @return 是否加锁成功
     */
    private boolean lock(String key) {
        BoundValueOperations<String, Number> oper = siRedis.boundValueOps("mutex_" + key);
        boolean r =  oper.setIfAbsent(1);
        if(r)
            oper.expire(10, TimeUnit.SECONDS);
        return r;
    }

    /**
     * 解除悲观锁
     * @throws RuntimeException 键不存在
     */
    private void unlock(String key) {
        siRedis.delete("mutex_" + key);
    }

    /**
     * 设置保存监控周期的Key
     * @param key 原Rule Key
     * @param period 每个周期的时间，单位秒
     * @param periodTime 该告警规则触发需要满足的周期数
     * @return 告警规则已经有连续几个周期满足
     */
    private int setPeriodKey(String key, int period, int periodTime) {
        key = "period_" + key;
        if(siRedis.hasKey(key)) {
            return siRedis.boundValueOps(key).increment(1).intValue();
        } else {
            BoundValueOperations<String, Number> oper = siRedis.boundValueOps(key);
            oper.set(1);
            oper.expire((int)(period * periodTime * 1.2), TimeUnit.SECONDS);
            return 1;
        }


    }

    /**
     * 删除保存监控周期的键
     * @param key 原Rule Key
     */
    private void deletePeriodKey(String key) {
        siRedis.delete("period_" + key);
    }

    /**
     * 设置告警信息发送沉默时间
     * @param key 原Rule Key
     * @param time 沉默周期
     * @return 是否确定发送告警信息
     */
    private boolean setSilenceKey(String key, int time) {
        key = "silence_" + key;
        if(siRedis.hasKey(key)) {
            return false;
        } else {
            BoundValueOperations<String, Number> oper = siRedis.boundValueOps(key);
            oper.set(time);
            oper.expire(time, TimeUnit.SECONDS);
            return true;
        }
    }
}
