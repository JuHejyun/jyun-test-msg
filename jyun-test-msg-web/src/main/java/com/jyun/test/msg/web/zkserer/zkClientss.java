package com.jyun.test.msg.web.zkserer;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

public class zkClientss implements ApplicationRunner, Ordered {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${zookeeper.server}")
	private static final String connectString = "10.182.96.168:2181";
	private static final int sessionTimeout = 2000;
	private static final String parent = "/zhuhj-msg-servers";
	static ZooKeeper zk = null;
	private static volatile List<String> serverList;

	public void getconnect() throws Exception {
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				try {
					//再次注册监听，并更新信息
					getznode();
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
	public void getznode() throws Exception {
		List<String> servers = new ArrayList<String>();
		//获取子节点信息，并对父节点进行监听
		List<String> children = zk.getChildren(parent, true); //使用true 就是使用上面的监听器
		for (String child : children) {
			logger.info(child);
			byte[] data = zk.getData(parent + "/" + child, false, null);
			//c把获取的数据给成员变量，以方便给各个客户端的业务使用
			servers.add(new String(data));
		}
		serverList = servers;
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
		zkClientss zc = new zkClientss();
		zc.getconnect();
		//查看zookeeper下/servers目录下的子目录
		zc.getznode();
		//业务线程使用
		zc.clientwork();
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
