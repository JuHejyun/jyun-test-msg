package com.jyun.test.msg.web.zkserer;

import com.alibaba.fastjson.JSON;
import com.jyun.test.msg.web.config.ZkServerConfig;
import com.jyun.test.msg.web.nsq.NsqConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

//被 spring 容器管理
@Component
//如果多个自定义的 ApplicationRunner  ，用来标明执行的顺序
@Order(2)
@EnableConfigurationProperties
public class ZkClientss implements ApplicationRunner {

	private final Logger logger = LogManager.getLogger(this.getClass());

	static ZooKeeper zk = null;
	private static volatile List<String> serverList;
	NsqConsumer nsqConsumer;

	@Autowired
	private ZkServerConfig zkServerConfig;

	public void getconnect(ZkServerConfig zkServerConfig) throws Exception {
		zk = new ZooKeeper(zkServerConfig.getServer(), zkServerConfig.getSessionTimeout(), new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					//再次注册监听，并更新信息
					getznode(zkServerConfig);
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 获取子节点的相关信息和数据
	 *
	 * @throws Exception
	 */
	public void getznode(ZkServerConfig zkServerConfig) throws Exception {
		List<String> servers = new ArrayList<String>();
		//获取子节点信息，并对父节点进行监听
		List<String> children = zk.getChildren(zkServerConfig.getParent(), true); //使用true 就是使用上面的监听器
		for (String child : children) {
			logger.info(child);
			byte[] data = zk.getData(zkServerConfig.getParent() + "/" + child, false, null);
			//c把获取的数据给成员变量，以方便给各个客户端的业务使用
			servers.add(new String(data));
		}
		serverList = servers;
		nsqConsumer.listener(serverList);
		logger.info(JSON.toJSONString(serverList));
	}

	/**
	 * 客户端业务
	 *
	 * @throws Exception
	 */
	public void clientwork() throws Exception {
		logger.info("client starts working .....");
		Thread.sleep(Long.MAX_VALUE);

	}


	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		// 获取zookeeper的链接
		ZkClientss zc = new ZkClientss();
		zc.getconnect(zkServerConfig);
		//查看zookeeper下/servers目录下的子目录
		zc.getznode(zkServerConfig);
		//业务线程使用
		zc.clientwork();

		nsqConsumer = new NsqConsumer();
	}

}
