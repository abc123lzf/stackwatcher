package com.lzf.stackwatcher.agent.data;

import org.libvirt.DomainBlockInfo;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月23日 上午11:27:10
* @Description 虚拟机磁盘使用量及其总容量数据
*/
public final class InstanceDiskCapacityData extends CurrentInstanceData {

	private static final long serialVersionUID = -7427561468469407864L;
	
	public final DeviceData[] data;
	
	public static final class DeviceData {	
		public final String device;
		
		public final long capacity;
		public final long allocation;
		public final long physical;
		
		public DeviceData(String device, long cap, long allo, long physical) {
			this.device = device;
			this.capacity = cap;
			this.allocation = allo;
			this.physical = physical;
		}
	}

	public InstanceDiskCapacityData(String host, int id, String uuid, String name, DeviceData... data) {
		super(host, id, uuid, name, INSTANCE_DISK_CAP);
		this.data = data;
	}
	
	public InstanceDiskCapacityData(String host, int id, String uuid, String name, String[] device, 
				DomainBlockInfo... info) {
		super(host, id, uuid, name, INSTANCE_DISK_CAP);
		data = new DeviceData[info.length];
		int i = 0;
		for(DomainBlockInfo dbi : info) {
			data[i] = new DeviceData(device[i], dbi.getCapacity(), 
					dbi.getAllocation(), dbi.getPhysical());
			i++;
		}
	}
}
