package com.jyun.test.msg.web.nsq;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 * Created by zhuhejun on 2018/7/26.
 */
@Component
@EnableConfigurationProperties
public class NsqConsumer {

	private final Logger logger = LogManager.getLogger(this.getClass());

	public static Map<String,NSQConsumer> consumerMap;


	public void listener(List<String> serverList){

		for (String ip : serverList) {

			NSQConsumer nsqConsumer = consumerMap.get(ip);
			if(null == nsqConsumer){
				NSQLookup lookup = new DefaultNSQLookup();
				lookup.addLookupAddress(ip, 4161);
				NSQConsumer consumer = new NSQConsumer(lookup, "TestTopic", "dustin", (message) -> {
					logger.info("received: " + message);
					//now mark the message as finished.
					message.finished();

					//or you could requeue it, which indicates a failure and puts it back on the queue.
					//message.requeue();
				});

				consumer.start();

				ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
						new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

				consumer.setExecutor(executorService);

				consumerMap.put(ip,consumer);
			}

		}


	}
}
