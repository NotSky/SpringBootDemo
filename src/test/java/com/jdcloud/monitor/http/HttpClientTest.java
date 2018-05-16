package com.jdcloud.monitor.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cloud.monitor.common.http.HttpResult;
import com.cloud.monitor.common.http.HttpService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpClientTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientTest.class);
	@Autowired
	private HttpService httpService;
	
	@Test
	public void testHttp() throws Exception {
		HttpResult result = httpService.doGet("http://localhost:8080/user/20");
		LOGGER.info(result.toString());
	}
}
