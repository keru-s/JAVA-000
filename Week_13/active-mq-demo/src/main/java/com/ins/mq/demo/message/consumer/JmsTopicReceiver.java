package com.ins.mq.demo.message.consumer;

import com.ins.mq.demo.domain.Email;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsTopicReceiver {

    @JmsListener(destination = "topic.mailbox", containerFactory = "jmsTopicListenerContainer")
    public void receiverMessage1(Email email) {
        System.out.println("Consumer-1 received <" + email + ">");
    }

    @JmsListener(destination = "topic.mailbox", containerFactory = "jmsTopicListenerContainer")
    public void receiverMessage2(Email email) {
        System.out.println("Consumer-2 received <" + email + ">");
    }

    @JmsListener(destination = "topic.mailbox", containerFactory = "jmsTopicListenerContainer")
    public void receiverMessage3(Email email) {
        System.out.println("Consumer-3 received <" + email + ">");
    }
}
