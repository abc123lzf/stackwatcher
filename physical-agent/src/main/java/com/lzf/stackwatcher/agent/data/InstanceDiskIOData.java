package com.lzf.stackwatcher.agent.data;

import org.libvirt.DomainBlockStats;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月23日 上午11:12:58
* @Description 类说明
*/
public final class InstanceDiskIOData extends CurrentInstanceData {
	
	private static final long serialVersionUID = -9205533290191430319L;
	
	public final DeviceData[] data;
	
	public static final class DeviceData {
		public final String device;	//磁盘设备名
		
		public final int rdReqSpeed;
		public final int rdByteSpeed;

		public final long rdReq;	//磁盘读请求数量
		public final long rdBytes;	//磁盘读出字节数
		
		public final int wrReqSpeed;
		public final int wrByteSpeed;
		
		public final long wrReq;	//磁盘写请求数
		public final long wrBytes;	//磁盘写字节数
		
		public final long errors;	//发生的异常数量
		
		public DeviceData(String device, long rdReq, long rdBytes, long wrReq, long wrBytes, 
				long errors, int rdReqSpeed, int rdByteSpeed, int wrReqSpeed, int wrByteSpeed) {
			this.device = device;
			this.rdReq = rdReq;
			this.rdBytes = rdBytes;
			this.wrReq = wrReq;
			this.wrBytes = wrBytes;
			this.errors = errors;
			this.rdReqSpeed = rdReqSpeed;
			this.rdByteSpeed = rdByteSpeed;
			this.wrReqSpeed = wrReqSpeed;
			this.wrByteSpeed = wrByteSpeed;
		}
	}
	
	
	
	public InstanceDiskIOData(String host, int id, String uuid, String name, DeviceData... data) {
		super(host, id, uuid, name, INSTANCE_DISK_IO);
		this.data = data;
	}
	
	public InstanceDiskIOData(String host, int id, String uuid, String name, String[] device, DomainBlockStats[] oldInfos,
			DomainBlockStats[] newInfos, long dt) {
		super(host, id, uuid, name, INSTANCE_DISK_IO);
		data = new DeviceData[newInfos.length];
		int i = 0;
		for(DomainBlockStats stats : newInfos) {
			DomainBlockStats old = oldInfos[i];
			data[i] = new DeviceData(device[i], stats.rd_req, stats.rd_bytes, stats.wr_req, 
					stats.wr_bytes, stats.errs, (int)((double)(stats.rd_bytes - old.rd_bytes) / dt * 1000),
					(int)((double)(stats.rd_req - old.rd_req) / dt * 1000), 
					(int)((double)(stats.wr_bytes - old.wr_bytes) / dt * 1000),
					(int)((double)(stats.wr_req - old.wr_req) / dt * 1000));
			i++;
		}
	}
}
