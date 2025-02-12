package com.silence004.springbootconsumerexample.service;

import com.silence004.rpcspringbootstarter.annotation.RpcReference;
import com.silence004.service.UserService;
import com.silence004.vo.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {
    @RpcReference(interfaceClass = UserService.class)
    private UserService userService;

    public void test(){
        User user = new User("我的名字是silence004");
        User user1 = userService.getUser(user);
        System.out.println(user1.getName());
    }
}
