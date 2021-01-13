package com.ins.mq.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JmsResponse {
    private String code;
    private String msg;

    public static JmsResponse success(){
        return new JmsResponse("200","success");
    }
}
