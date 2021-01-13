package com.ins.mq.demo.controller;

import com.ins.mq.demo.domain.Email;
import com.ins.mq.demo.domain.JmsResponse;
import com.ins.mq.demo.message.JmsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JmsController {

    @Autowired
    private JmsProducer jmsProducer;

    @PostMapping("/sendEmail")
    public JmsResponse sendEmail(Email email) {
        jmsProducer.sendEmail(email);
        return JmsResponse.success();
    }
}
