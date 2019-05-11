package com.lzf.stackwatcher.agent.data;

import org.libvirt.MemoryStatistic;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月22日 下午11:08:45
* @Description 虚拟机内存监控数据(监控数据尚不准确)
*/
public final class InstanceRAMData extends CurrentInstanceData {
	
	private static final long serialVersionUID = -8287591111412854163L;
	
	public final long size;
	public final long used;
	
	public InstanceRAMData(String host, int id, String uuid, String name, long size, long used) {
		super(host, id, uuid, name, INSTANCE_RAM);
		this.used = used;
		this.size = size;
	}
	
	public InstanceRAMData(String host, int id, String uuid, String name, MemoryStatistic[] info) {
		super(host, id, uuid, name, INSTANCE_RAM);
		if(info.length >= 1) {
			this.size = info[0].getValue();
			if(info.length >= 6)
				this.used = size - info[5].getValue();
			else
				this.used = -1;
		} else {
			size = -1;
			used = -1;
		}
	}
}
