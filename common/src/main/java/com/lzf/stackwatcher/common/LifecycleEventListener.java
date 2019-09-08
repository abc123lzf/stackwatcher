package com.lzf.stackwatcher.common; /**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

import java.util.EventListener;

/**
 * LifecycleEvent事件监听器
 * @author 李子帆
 * @time 2018年11月19日 下午9:03:52
 */
@FunctionalInterface
public interface LifecycleEventListener extends EventListener {
	
	void lifecycleEvent(LifecycleEvent event);
}
