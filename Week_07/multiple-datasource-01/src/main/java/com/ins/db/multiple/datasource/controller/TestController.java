package com.ins.db.multiple.datasource.controller;

import com.ins.db.multiple.datasource.domain.User;
import com.ins.db.multiple.datasource.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private UserManager userManager;

    @GetMapping("/ids")
    public List<User> getIds() {
        return userManager.listUser();
    }

    @PostMapping("/id/")
    public boolean insertUser(Integer id){
        int result = userManager.insert(id);
        return result > 0;
    }

}
