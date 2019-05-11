/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.insagent;

import java.util.EventObject;

/**
 * 
 * @author 李子帆
 * @time 2018年11月20日 上午10:47:53
 */
public class ContainerEvent extends EventObject {

	private static final long serialVersionUID = -2255173911922329902L;
	
	private final String type;
	
	private final Object data;
	
	public ContainerEvent(Container<?> source, String type, Object data) {
		super(source);
		this.type = type;
		this.data = data;
	}

	/**
	 * @param arg0
	 */
	public ContainerEvent(Container<?> source, String type) {
		this(source, type, null);
	}
	
	public String getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}
}
