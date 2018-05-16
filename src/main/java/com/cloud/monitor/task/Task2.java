package com.cloud.monitor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cloud.monitor.common.redis.RedisUtil;
import com.cloud.monitor.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Task2 {

	private final static Logger LOGGER = LoggerFactory.getLogger(Task2.class); 
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    
    private int count = 0;
    //@Scheduled(fixedRate = 6000)
    public void reportCurrentTime() {
        System.out.println("date：" + DATEFORMAT.format(new Date()));
        redisUtil.setString("test", "test111111111");
        redisUtil.saddString("testsadd", count+++"");
        LOGGER.info(userService.getAllUsers().toString());
    }

}
