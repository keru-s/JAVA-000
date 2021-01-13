package com.ins.mq.demo.controller;

import com.ins.mq.demo.domain.Email;
import com.ins.mq.demo.domain.JmsResponse;
import com.ins.mq.demo.message.producer.JmsProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class JmsController {

    @Resource(name = "queueProducer")
    private JmsProducer queueProducer;

    @Resource(name = "topicProducer")
    private JmsProducer topicProducer;


    @PostMapping("/sendEmail")
    public JmsResponse sendEmail(Email email) {
        queueProducer.sendEmail(email);
        return JmsResponse.success();
    }

    @PostMapping("/sendEmailToTopic")
    public JmsResponse sendEmailToTopic(Email email) {
        topicProducer.sendEmail(email);
        return JmsResponse.success();
    }
}
