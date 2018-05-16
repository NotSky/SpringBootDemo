package com.cloud.monitor.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Task1 {

    private int count=0;
   
    //@Scheduled(cron="*/6 * * * * ?")
    @Scheduled(cron="0 12 16 * * ?")
    private void process(){
        System.out.println("task runing  "+(count++));
    }

}
