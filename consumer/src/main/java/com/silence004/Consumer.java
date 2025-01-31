package com.silence004;

import com.silence004.config.RpcConfig;
import com.silence004.proxy.ServiceProxy;
import com.silence004.proxy.ServiceProxyFactory;
import com.silence004.proxy.UserServiceProxy;
import com.silence004.register.LocalRegistry;
import com.silence004.service.UserService;
import com.silence004.vo.User;



public class Consumer {

    public static void main(String[] args) {
//        UserService userService=null;

//        UserServiceProxy userServiceProxy = new UserServiceProxy();
//        ServiceProxy serviceProxy = new ServiceProxy();
//        User user=new User("silence004");

        //动态代理
//        UserService proxy = ServiceProxyFactory.getProxy(UserService.class);
//        User newUser = proxy.getUser(user);
//        if(newUser!=null){
//            System.out.println(newUser.getName());
//        }else{
//            System.out.println("user=null");
//        }

        //全局配置
//        RpcConfig rpc = RpcApplication.getRpcConfig();

        UserService proxy = ServiceProxyFactory.getProxy(UserService.class);
        System.out.println(proxy.getUser(new User("silence004")));
        System.out.println(proxy.getUser(new User("silence004")));
        System.out.println(proxy.getUser(new User("silence004")));




    }

}
