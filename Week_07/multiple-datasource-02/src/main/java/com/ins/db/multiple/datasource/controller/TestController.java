package com.ins.db.multiple.datasource.controller;

import com.ins.db.multiple.datasource.domain.User;
import com.ins.db.multiple.datasource.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
