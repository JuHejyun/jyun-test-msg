package com.jyun.test.msg.web.nsq;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import com.jyun.test.msg.web.config.ExecutorConfig;
import org.apache.catalina.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Created by zhuhejun on 2018/7/26.
 */
@Component
@EnableConfigurationProperties
public class NsqConsumer {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	ExecutorConfig executorConfig;
	static Map<String,NSQConsumer> consumerMap;


	public void listener(List<String> serverList){

		for (String ip : serverList) {
			NSQLookup lookup = new DefaultNSQLookup();
			lookup.addLookupAddress(ip, 4161);
			NSQConsumer consumer = new NSQConsumer(lookup, "TestTopic", "dustin", (message) -> {
				System.out.println("received: " + message);
				//now mark the message as finished.
				message.finished();

				//or you could requeue it, which indicates a failure and puts it back on the queue.
				//message.requeue();
			});

			consumer.start();

			//线程睡眠，让程序执行完
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}


	}
}
