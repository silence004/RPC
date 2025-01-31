package com.silence004.proxy;

import com.silence004.RpcApplication;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {

    public static<T> T getProxy(Class<T> serviceClass){

        //检验参数配置，走mock代理
        if(RpcApplication.getRpcConfig().getMock()){
            return MockServiceProxyFactory.getProxy(serviceClass);
        }

        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());

    }
}
