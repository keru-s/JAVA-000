package com.ins.mq.demo.message.producer.impl;

import com.ins.mq.demo.domain.Email;
import com.ins.mq.demo.message.producer.JmsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("queueProducer")
public class QueueProducer implements JmsProducer {

    @Resource(name = "jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    public void sendEmail(Email email) {
        System.out.println("Sending an email message.");
        jmsTemplate.convertAndSend("mailbox", email);
    }
}
