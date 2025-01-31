package com.silence004.proxy;

import java.lang.reflect.Proxy;

public class MockServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass){
       return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
               new Class[]{serviceClass},
               new MockServiceProxy());
    }
}
