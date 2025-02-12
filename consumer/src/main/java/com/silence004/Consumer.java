package com.silence004;

import com.silence004.bootstrap.ConsumerBootstrap;
import com.silence004.config.RpcConfig;
import com.silence004.proxy.ServiceProxy;
import com.silence004.proxy.ServiceProxyFactory;
import com.silence004.proxy.UserServiceProxy;
import com.silence004.register.LocalRegistry;
import com.silence004.service.UserService;
import com.silence004.vo.User;



public class Consumer {

    public static void main(String[] args) {
        ConsumerBootstrap.init();

        UserService proxy = ServiceProxyFactory.getProxy(UserService.class);
        System.out.println(proxy.getUser(new User("silence004")));
//        System.out.println(proxy.getUser(new User("silence001")));
//        System.out.println(proxy.getUser(new User("silence002")));




    }

}
