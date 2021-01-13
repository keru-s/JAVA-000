package com.ins.mq.demo.message.consumer;

import com.ins.mq.demo.domain.Email;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsQueueReceiver {

    @JmsListener(destination = "mailbox",containerFactory = "jmsQueueListenerContainer")
    public void receiverMessage(Email email) {
        System.out.println("Received <" + email + ">");
    }
}
