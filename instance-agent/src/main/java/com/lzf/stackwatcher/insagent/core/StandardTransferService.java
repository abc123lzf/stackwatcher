package com.lzf.stackwatcher.insagent.core;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import com.lzf.stackwatcher.insagent.Agent;
import com.lzf.stackwatcher.insagent.ContainerBase;
import com.lzf.stackwatcher.insagent.TransferService;
import com.lzf.stackwatcher.insagent.data.Data;

/**
* @author 李子帆
* @version 1.0
* @date 2018年12月24日 上午9:56:09
* @Description 类说明
*/
public class StandardTransferService extends ContainerBase<Agent> implements TransferService {
	private static final Logger log = Logger.getLogger(StandardTransferService.class);
	
	private final Agent agent;
	
	private String transferServer;
	
	private int transferPort;
	
	private volatile NioEventLoopGroup loopGroup;
	
	private volatile Channel channel;
	
	private final MointorDataToByteHandler mointorDataToByteHandler = new MointorDataToByteHandler();
	
	public StandardTransferService(Agent agent) {
		this.agent = agent;
		setName("TransferService");
		addLifecycleEventListener(LifecycleLoggerListener.INSTANCE);
	}
	
	@Override
	protected void initInternal() {
		
		Properties pro = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
		try {
			pro.load(is);
		} catch (IOException e) {
			log.error("读取配置文件config.properties出错", e);
			System.exit(1);
		}
		
		transferServer = pro.getProperty("server");
		transferPort = Integer.valueOf(pro.getProperty("port"));
		
		initNettyClient();
	}
	
	private void initNettyClient() {
		log.info(String.format("正在与监控服务器连接，主机名 %s, 端口%d", transferServer, transferPort));
		Bootstrap boot = new Bootstrap();
		boot.group(loopGroup = new NioEventLoopGroup(2))
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(mointorDataToByteHandler);
				}
			});
		
		ChannelFuture f = boot.connect(transferServer, transferPort);
		f.addListener(new ChannelFutureListener() {			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()) {
					EventLoop loop = future.channel().eventLoop();
					loop.schedule(new Runnable() {				
						@Override
						public void run() {
							log.warn("与监控服务器断开连接，正在进行重新连接...");
							try {
								loopGroup.shutdownGracefully().sync();
							} catch (InterruptedException ignore) {
							}
							
							initNettyClient();
						}
					}, 1L, TimeUnit.SECONDS);
				} else {
					channel = future.channel();
					log.info("与监控服务器连接成功");
				}
			}
		});
	}
	
	private final class MointorDataToByteHandler extends MessageToByteEncoder<Data> {
		@Override
		protected void encode(ChannelHandlerContext ctx, Data msg, ByteBuf out) throws Exception {
			log.info("数据解析...");
			byte[] json = msg.toJSON().getBytes();
			byte type = msg.type().getTypeByte();
			out.writeByte(type);
			short len = (short) json.length;
			out.writeShort(len);
			out.writeBytes(json);
		}
	} 
	
	@Override
	public void transferMointorData(Data data) {
		channel.writeAndFlush(data);
	}
	
	@Override
	public Agent getParent() {
		return agent;
	}
	
	
}
