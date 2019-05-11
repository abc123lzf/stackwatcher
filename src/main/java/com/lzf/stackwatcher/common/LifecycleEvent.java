package com.lzf.stackwatcher.common; /**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

import java.util.EventObject;

/**
 * 生命周期事件对象
 * @author 李子帆
 * @time 2018年11月19日 下午8:54:37
 */
public class LifecycleEvent extends EventObject {

	private static final long serialVersionUID = 5634527270748787476L;

	private final String type;
	private final Object data;
	
	public LifecycleEvent(Lifecycle source, String type) {
		this(source, type, null);
	}
	
	public LifecycleEvent(Lifecycle source, String type, Object data) {
		super(source);
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

}
