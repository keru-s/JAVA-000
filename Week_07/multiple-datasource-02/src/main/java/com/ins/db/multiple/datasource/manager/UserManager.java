package com.ins.db.multiple.datasource.manager;

import com.ins.db.multiple.datasource.domain.User;

import java.util.List;

public interface UserManager {
    List<User> listUser();

    int insert(int id);
}
