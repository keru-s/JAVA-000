package com.ins.demo.domain.config;

import com.ins.demo.domain.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author 01387005
 * @since 2020-11-18 14:31
 **/
@Configuration
@EnableConfigurationProperties({StudentProperties.class})
public class SchoolConfiguration {

    @Bean
    public Student student100(StudentProperties properties){
        Student result = new Student();
        result.setId(properties.getId());
        result.setName(properties.getName());
        return result;
    }

    @Bean
    @ConditionalOnMissingBean
    public Klass klass(Student student){
        Klass result = new Klass();
        result.setStudents(Collections.singletonList(student));
        return result;
    }

    @Bean
    @ConditionalOnClass({Klass.class,Student.class})
    public ISchool school(){
        return new School();
    }
}
