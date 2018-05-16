package com.cloud.monitor.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.cloud.monitor.common.http.HttpService;
import com.cloud.monitor.common.redis.RedisUtil;

/**
 * @author GouBoting
 * 计费资源单数据处理任务
 * */
@Component
public class BillingJob {
	private final static Logger LOGGER = LoggerFactory.getLogger(BillingJob.class);
	@Resource
	private HttpService httpService;
	@Autowired
	private RedisUtil redisUtil;
	
	//@Scheduled(cron="* * * * * ?")
    private void process(){
		int count = 100;
		CountDownLatch countdownlatch=new CountDownLatch(count);
    	ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("billingjob-pool-%d").build();
    	ExecutorService pool = new ThreadPoolExecutor(5, 200,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
		pool.execute(null);
		try{
			countdownlatch.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		pool.shutdown();
		//设置BillingJob结束标识
		//redisUtil.setString(key, value, seconds);
    }
}
