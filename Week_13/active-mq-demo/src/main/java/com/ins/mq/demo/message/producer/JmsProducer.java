package com.ins.mq.demo.message.producer;

import com.ins.mq.demo.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


public interface JmsProducer {

    void sendEmail(Email email);

}
