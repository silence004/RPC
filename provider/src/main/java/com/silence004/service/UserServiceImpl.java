package com.silence004.service;

import com.silence004.vo.User;

public class UserServiceImpl implements UserService {


    @Override
    public User getUser(User user) {
        System.out.println("user:"+user.getName());
        return user;
    }
}
