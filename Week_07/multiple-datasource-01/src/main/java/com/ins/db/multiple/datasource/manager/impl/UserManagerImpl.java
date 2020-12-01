package com.ins.db.multiple.datasource.manager.impl;

import com.ins.db.multiple.datasource.domain.User;
import com.ins.db.multiple.datasource.manager.UserManager;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserManagerImpl implements UserManager {
    @Resource(name = "masterTemplate")
    private JdbcTemplate masterTemplate;

    @Resource(name = "slaveTemplate")
    private JdbcTemplate slaveTemplate;

    @Override
    public List<User> listUser() {
        return slaveTemplate.query("select * from t1", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public int insert(int id) {
        return masterTemplate.update("insert into t1 value (?)", id);
    }
}
