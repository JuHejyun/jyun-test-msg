package com.jyun.test.msg.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhuhejun on 2018/7/26.
 */
@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZkServerConfig {
	private String server;
	private String parent;
	private Integer sessionTimeout;


	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Integer getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Integer sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}
