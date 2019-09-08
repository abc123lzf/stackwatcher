package com.lzf.stackwatcher.common; /**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

import java.util.EventListener;

/**
 * 
 * @author 李子帆
 * @time 2018年11月20日 上午10:46:23
 */
@FunctionalInterface
public interface ContainerEventListener extends EventListener {
	
	void containerEvent(ContainerEvent event);
	
}
