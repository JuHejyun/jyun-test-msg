package com.jyun.test.msg.web.nsq;

import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeoutException;

/**
 * Created by zhuhejun on 2018/7/26.
 */
public class NsqProducer {

	private final Logger logger = LogManager.getLogger(this.getClass());

	static NSQProducer producer;

	public void start(){
		producer = new NSQProducer().addAddress("localhost", 4161).start();
		try {
			producer.produce("TestTopic", ("this is a message").getBytes());
		} catch (NSQException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}
