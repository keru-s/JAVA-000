package com.ins.mq.demo.config;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Topic;

@Configuration
public class JmsTopic {
    @Bean
    public Topic mailbox() {
        return new ActiveMQTopic("topic.mailbox");
    }
}
