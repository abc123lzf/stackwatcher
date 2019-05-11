/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.insagent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author 李子帆
 * @time 2018年11月20日 上午10:54:55
 */
public abstract class ContainerBase<P> extends LifecycleBase implements Container<P> {

	protected String name;
	
	private List<ContainerEventListener> listeners = new CopyOnWriteArrayList<>();
	
	protected ContainerBase() {
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public final void addContainerEventListener(ContainerEventListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public final void removeContainerEventListener(ContainerEventListener listener) {
		int i = 0;
		for(ContainerEventListener c : listeners) {
			if(c == listener || c.equals(listener))
				listeners.remove(i);
			i++;
		}
	}
	
	protected final void fireContainerEvent(String type, Object data) {
		ContainerEvent event = new ContainerEvent(this, type, data);
		for(ContainerEventListener c : listeners)
			c.containerEvent(event);
	}
	
	@Override
	public P getParent() {
		throw new UnsupportedOperationException();
	}
}
