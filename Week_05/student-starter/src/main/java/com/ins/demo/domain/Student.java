package com.ins.demo.domain;

import lombok.Data;

/**
 * @author 01387005
 * @since 2020-11-18 14:34
 **/
@Data
public class Student {
    private int id;
    private String name;

    public void study() {
        System.out.println("id:" + id + ",name:" + name + " is studying");
    }
}
