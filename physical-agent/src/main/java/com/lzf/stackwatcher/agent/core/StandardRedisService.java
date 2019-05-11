package com.lzf.stackwatcher.agent.core;

import java.util.List;

import com.lzf.stackwatcher.agent.Agent;
import com.lzf.stackwatcher.common.ContainerBase;
import com.lzf.stackwatcher.agent.RedisService;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
* @author 李子帆
* @version 1.0
* @date 2018年11月20日 下午6:16:11
* @Description 类说明
*/
public class StandardRedisService extends ContainerBase<Agent> implements RedisService {
	
	private static final Logger log = Logger.getLogger(StandardRedisService.class);

	private final Agent agent;

	private JedisPool pool;

	public StandardRedisService(Agent agent) {
		setName("RedisService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
		this.agent = agent;
		agent.registerConfig(new RedisConfig(agent));
	}
	
	@Override
	protected void initInternal() {
		Config c = agent.getConfig(DEFAULT_CONFIG_NAME, Config.class);
		log.info(String.format("[组件:%s] %s", getName(), c.toString()));
		JedisPoolConfig jc = new JedisPoolConfig();
		jc.setMaxTotal(c.maxTotalConnect());
		jc.setMaxIdle(c.maxIdleConnect());
		jc.setMinIdle(c.minIdleConnect());

		pool = new JedisPool(jc, c.host(), c.port(), 10000, c.pass(), c.databaseId());
	}
	
	@Override
	protected void stopInternal() {
		Jedis jedis = pool.getResource();
		try {
			jedis.flushDB();
		} finally {
			jedis.close();
			pool.close();
		}	
	}
	
	@Override
	protected void restartInternal() {
		pool.close();
		init();
		start();
	}
	
	@Override
	public void insertList(String key, String... entries) {
		Jedis jedis = pool.getResource();
		try {
			jedis.lpush(key, entries);
		} finally {
			jedis.close();
		}
	}
	
	@Override
	public List<String> getList(String key) {
		Jedis jedis = pool.getResource();
		try {
			return jedis.lrange(key, 0, -1);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void insertPair(String key, String value) {
		Jedis jedis = pool.getResource();
		try {
			jedis.set(key, value);
		} finally {
			jedis.close();
		}
	}


	@Override
	public void delete(String key) {
		Jedis jedis = pool.getResource();
		try {
			jedis.del(key);
		} finally {
			jedis.close();
		}
	}
}
