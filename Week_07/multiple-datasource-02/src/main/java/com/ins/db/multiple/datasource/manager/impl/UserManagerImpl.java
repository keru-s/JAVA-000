package com.ins.db.multiple.datasource.manager.impl;

import com.ins.db.multiple.datasource.domain.User;
import com.ins.db.multiple.datasource.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserManagerImpl implements UserManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> listUser() {
        return jdbcTemplate.query("select * from t1", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public int insert(int id) {
        return jdbcTemplate.update("insert into t1 value (?)", id);
    }
}
