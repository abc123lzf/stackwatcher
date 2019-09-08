package com.lzf.stackwatcher.common; /**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */

/**
 * 
 * @author 李子帆
 * @time 2018年11月20日 上午12:43:48
 */
public interface Container<P> extends Lifecycle {
	/**
	 * 添加子容器事件
	 */
	String ADD_CHILD_EVENT = "add_child_event";
	
	/**
	 * 移除子容器事件
	 */
	String REMOVE_CHILD_EVENT = "remove_child_event";
	
	/**
	 * 设置容器名称
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * 获取容器名称
	 * @return 
	 */
	String getName();
	
	/**
	 * 获取父容器
	 * @return 父容器引用
	 */
	P getParent();

	/**
	 * 添加容器添加事件监听器
	 * @param listener 监听器实例
	 */
	void addContainerEventListener(ContainerEventListener listener);

	/**
	 * 移除容器事件监听器
	 * @param listener 监听器实例
	 */
	void removeContainerEventListener(ContainerEventListener listener);
}
