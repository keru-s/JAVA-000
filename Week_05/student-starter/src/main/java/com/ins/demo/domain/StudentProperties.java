package com.ins.demo.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 01387005
 * @since 2020-11-18 14:25
 **/
@ConfigurationProperties(prefix = "insight.student")
@Data
public class StudentProperties {
    private int id;
    private String name;
}
