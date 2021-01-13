package com.ins.mq.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ActiveMqDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMqDemoApplication.class, args);
    }

}
