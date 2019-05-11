package com.lzf.stackwatcher.insagent.bootstrap;


import com.lzf.stackwatcher.insagent.core.StandardAgent;
import com.lzf.stackwatcher.insagent.Agent;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月12日 下午9:12:57
* @Description 引导类
*/
public final class Bootstrap {
	
	private static Agent agent;

	public static void main(String[] args) {
		agent = new StandardAgent();
		agent.init();
		agent.start();
	}

	public static Agent getAgent() {
		return agent;
	}
}
