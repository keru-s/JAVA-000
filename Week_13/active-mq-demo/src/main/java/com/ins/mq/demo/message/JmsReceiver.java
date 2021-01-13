package com.ins.mq.demo.message;

import com.ins.mq.demo.domain.Email;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsReceiver {

    @JmsListener(destination = "mailbox")
    public void receiverMessage(Email email){
        System.out.println("Received <" + email + ">");
    }
}
