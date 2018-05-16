package com.cloud.monitor.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

	private String host;

	private int port;

	private String password;

	private int timeout;

	@Bean
	public JedisPoolConfig getRedisConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		return config;
	}

	@Bean
	public JedisPool getJedisPool() {
		JedisPoolConfig config = getRedisConfig();

		JedisPool pool = new JedisPool(config, host, port, timeout, password);
		LOGGER.info("init JredisPool ...host:{}|port:{}",host,port);
		return pool;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}