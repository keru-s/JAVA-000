package com.ins.mq.demo.message.producer.impl;

import com.ins.mq.demo.domain.Email;
import com.ins.mq.demo.message.producer.JmsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Topic;

@Service("topicProducer")
public class TopicProducer implements JmsProducer {

    @Resource(name = "jmsTopicTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    private Topic topic;

    @Override
    public void sendEmail(Email email) {
        System.out.println("Sending an email message to topic.");
        jmsTemplate.convertAndSend(topic,email);
    }
}
