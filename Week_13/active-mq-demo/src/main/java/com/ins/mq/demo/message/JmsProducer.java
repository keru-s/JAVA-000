package com.ins.mq.demo.message;

import com.ins.mq.demo.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendEmail(Email email) {
        System.out.println("Sending an email message.");
        jmsTemplate.convertAndSend("mailbox", email);
    }
}
