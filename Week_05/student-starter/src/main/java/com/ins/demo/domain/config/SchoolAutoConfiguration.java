package com.ins.demo.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 01387005
 * @since 2020-11-18 14:39
 **/
@Configuration
@Import(SchoolConfiguration.class)
public class SchoolAutoConfiguration {
}
