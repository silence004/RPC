package com.silence004.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    public static final Map<String,Class<?>> map=new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName,Class<?> implClass){
        map.put(serviceName,implClass);
    }

    /**
     * 获取服务
     * @param serviceName
     */
    public static Class<?> get(String serviceName) {return map.get(serviceName);}

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }

}
