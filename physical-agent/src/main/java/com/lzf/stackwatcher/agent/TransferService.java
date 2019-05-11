/**
 * 第十届中国大学生服务外包创新创业大赛
 * 团队：s1mple  选题：A02
 */
package com.lzf.stackwatcher.agent;

import com.lzf.stackwatcher.common.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 将监控数据传输给Transfer-Server
 * @author 李子帆
 * @time 2018年11月20日 上午11:41:17
 */
public interface TransferService extends Service<Agent> {
	String DEFAULT_SERVICE_NAME = "service.transfer";
	String DEFAULT_CONFIG_NAME = "config.transfer";
	
	String INS_CPU_DATA_QUEUE = "instance_cpu_data";
	String INS_RAM_DATA_QUEUE = "instance_memory_data";
	String INS_NETIO_DATA_QUEUE = "instance_network_io_data";
	String INS_DISKIO_DATA_QUEUE = "instance_disk_io_data";
	String INS_DISKCAP_DATA_QUEUE = "instance_disk_capacity_data";
	
	String NOVA_CPU_DATA_QUEUE = "nova_cpu_data";
	String NOVA_RAM_DATA_QUEUE = "nova_memory_data";
	String NOVA_NETIO_DATA_QUEUE = "nova_network_io";
	String NOVA_DISKIO_DATA_QUEUE = "nova_disk_io_data";
	String NOVA_DISKCAP_DATA_QUEUE = "nova_disk_capacity_data";
	
	String INSAGENT_CPU_DATA_QUEUE = "instance_agent_cpu_data";
	String INSAGENT_RAM_DATA_QUEUE = "instance_agent_memory_data";
	String INSAGENT_NETWORK_DATA_QUEUE = "instance_agent_network_data";
	String INSAGENT_DISK_DATA_QUEUE = "instance_agent_disk_data";
	
	@Override
	default String serviceName() {
		return DEFAULT_SERVICE_NAME;
	}
	
	enum InstanceAgentDataType {
		CPU((byte)0x01), 
		MEMORY((byte)0x02), 
		NETWORK((byte)0x03), 
		DISK((byte)0x04);
		private static final Map<Byte, InstanceAgentDataType> map = new HashMap<>();
		
		public final byte typeByte;
		
		InstanceAgentDataType(byte type) {
			this.typeByte = type;
			init();
		}
		
		private void init() {
			map.put(typeByte, this);
		}
		
		public static InstanceAgentDataType getByByteType(byte b) {
			return map.get(b);
		}
	}

	interface Config extends com.lzf.stackwatcher.common.Config {
		
		String kafkaAddresses();

		Properties connectProperties();
		
		int sendRate();
	}
	
	/**
	 * 接受云主机的监控数据并向服务器发送数据
	 * @param type 数据类型
	 * @param json JSON数据
	 */
	void transferInstanceAgentData(InstanceAgentDataType type, String json);
}
