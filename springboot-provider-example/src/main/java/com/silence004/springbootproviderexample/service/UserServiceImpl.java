package com.silence004.springbootproviderexample.service;

import com.silence004.rpcspringbootstarter.annotation.RpcService;
import com.silence004.service.UserService;
import com.silence004.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RpcService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名："+user.getName());
        return user;
    }
}
