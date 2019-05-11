package com.lzf.stackwatcher.agent.data;

/**
* @author 李子帆
* @version 1.0
* @date 2018年11月22日 下午10:48:22
* @Description 虚拟机CPU监控数据，需转换为JSON
*/
public final class InstanceVCPUData extends CurrentInstanceData {
	private static final long serialVersionUID = 7759205010748452971L;
	
	public final VCPU[] cpus;	//即时CPU信息
	public final double usage;
	
	public static final class VCPU {		
		public final int id;	//CPU编号
		public final int state;		//CPU状态，0代表关闭，1代表
		public final double usage;		//CPU使用率
		
		public VCPU(int number, int state, double usage) {
			this.id = number;
			if(usage > 100.0)
				this.usage = 100.0;
			else
				this.usage = (double)Math.round(usage * 100) / 100;
			this.state = state;
		}
	}
	
	public InstanceVCPUData(String host, int id, String uuid, String name, VCPU... cpus) {
		super(host, id, uuid, name, INSTANCE_CPU);
		double t = 0;
		int on = 0;
		for(VCPU vcpu : cpus) {
			if(vcpu.state == 1) {
				t += vcpu.usage;
				on++;
			}
		}
		this.cpus = cpus;
		this.usage = t / on;
	}
}
