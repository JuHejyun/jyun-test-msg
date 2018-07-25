package com.jyun.test.msg.web.zkserer;
 
import java.io.IOException;
import java.net.InetAddress;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

public class zkServers implements ApplicationRunner, Ordered {

	@Value("${zookeeper.server}")
	private static final String connectString = "10.182.96.168:2181";
	private static final int sessionTimeout =2000;
	private static final String parent="/zhuhj-msg-servers";
	static ZooKeeper zk =null;
	
	public static void testparent() throws Exception{
		//判断是否存在这个父目录
		Stat exists = zk.exists(parent, false);
		//如果不存在则创建
		if(exists == null){
			zk.create(parent, "IIIII".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);	
			System.out.println(" not exit ,then create----");
		}		
	}
	
	public static void getconnect() throws Exception {
		zk =new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				System.out.println(event + "++++++" + event.getPath());
				try {
					zk.getChildren(parent, true);
				} catch (Exception e) {				
				}
			}
		});
	}
	
	public static void registserver(String args) throws Exception{
		//在父目录下创建一个临时的序列化子节点
		String createsever = zk.create(parent+"/server", args.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(args + " is online " + createsever );
	}
	
	public static void serverwork(String args) throws Exception {
		System.out.println(args + " start working .....");
		Thread.sleep(Long.MAX_VALUE);
		
	}


	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {

		String addr = InetAddress.getLocalHost().getHostAddress();//获得本机IP

		//// 获取zookeeper的链接
		getconnect();
		//先判断着个父目录是否存在，不存在就创建
		testparent();
		//在zookeeper上注册该服务器
		registserver(addr);
		//执行这个服务器的相关业务
		serverwork(addr);
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
