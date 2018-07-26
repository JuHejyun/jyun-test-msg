package com.jyun.test.msg.web.zkserer;

import com.jyun.test.msg.web.config.ZkServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

//被 spring 容器管理
@Component
//如果多个自定义的 ApplicationRunner  ，用来标明执行的顺序
@Order(1)
@EnableConfigurationProperties
public class ZkServers implements ApplicationRunner {

	private final static Logger logger = LogManager.getLogger(ZkServers.class);

	static ZooKeeper zk =null;

	@Autowired
	private ZkServerConfig zkServerConfig;
	
	public static void testparent(ZkServerConfig zkServerConfig) throws Exception{
		//判断是否存在这个父目录
		Stat exists = zk.exists(zkServerConfig.getParent(), false);
		//如果不存在则创建
		if(exists == null){
			zk.create(zkServerConfig.getParent(), "IIIII".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			logger.info(" not exit ,then create----");
		}		
	}
	
	public static void getconnect(ZkServerConfig zkServerConfig) throws Exception {
		zk =new ZooKeeper(zkServerConfig.getServer(), zkServerConfig.getSessionTimeout(), new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				logger.info(event + "++++++" + event.getPath());
				try {
					zk.getChildren(zkServerConfig.getParent(), true);
				} catch (Exception e) {				
				}
			}
		});
	}
	
	public static void registserver(ZkServerConfig zkServerConfig,String args) throws Exception{
		//在父目录下创建一个临时的序列化子节点
		String createsever = zk.create(zkServerConfig.getParent()+"/server", args.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		logger.info(args + " is online " + createsever );
	}
	
	public static void serverwork(String args) throws Exception {
		logger.info(args + " start working .....");
//		Thread.sleep(Long.MAX_VALUE);
		
	}


	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {

		String addr = InetAddress.getLocalHost().getHostAddress();//获得本机IP

		//// 获取zookeeper的链接
		getconnect(zkServerConfig);
		//先判断着个父目录是否存在，不存在就创建
		testparent(zkServerConfig);
		//在zookeeper上注册该服务器
		registserver(zkServerConfig,addr);
		//执行这个服务器的相关业务
		serverwork(addr);
	}

}
