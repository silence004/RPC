package com.silence004.service;

import com.silence004.vo.User;

public interface UserService {

    User getUser(User user);

    /**
     * mock测试方法
     * @return
     */
    default short getNumber(){return -1;}
}
