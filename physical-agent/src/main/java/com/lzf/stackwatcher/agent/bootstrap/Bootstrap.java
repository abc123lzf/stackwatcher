package com.lzf.stackwatcher.agent.bootstrap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import com.lzf.stackwatcher.agent.core.StandardAgent;
import org.apache.log4j.Logger;
import com.lzf.stackwatcher.agent.Agent;


/**
 * Agent模块启动引导类
 * @author 李子帆
 * @time 2018年11月19日 下午8:43:01
 */
public class Bootstrap {

	private static final Logger log = Logger.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		String hostName = null;

		try {
			InetAddress ia = InetAddress.getLocalHost();
			hostName = ia.getCanonicalHostName();
		} catch (UnknownHostException e) {
			log.error("无法获取当前计算机主机名", e);
			System.exit(1);
		}
		
		log.info(String.format("计算节点Agent程序启动，主机名:%s", hostName));
		Agent agent = new StandardAgent();

		agent.init();
		agent.start();
	}
	
	
	private Bootstrap() {
		throw new UnsupportedOperationException();
	}
}
