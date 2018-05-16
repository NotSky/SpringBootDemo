package com.cloud.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author GouBoting
 * Server Start
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ResourceMonitorServer {

    public static void main(String[] args) {
        SpringApplication.run(ResourceMonitorServer.class,args);
    }
}
