package com.silence004.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.silence004.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.management.RuntimeErrorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    /**
     * 存储已经加载的类：key=》实现类
     */
    private static final Map<String,Map<String,Class<?>>>loaderMap=new ConcurrentHashMap<>();

    /**
     * 对象实例缓存，单例模式
     */
    private static final Map<String,Object>instanceCache=new ConcurrentHashMap<>();

    /**
     * 系统spi目录
     */
    private static final String RPC_SYSTEM_SPI_DIR="META-INF/rpc/system/";

    /**
     * 用户自定义spi目录
     */
    private static final String RPC_CUSTOM_SPI_DIR="META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIR=new String[]{RPC_SYSTEM_SPI_DIR,RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST= Arrays.asList(Serializer.class);

    /**
     *加载所有类型
     */
    public static void loadAll(){
        log.info("加载所有spi类");
        for(Class<?> nClass:LOAD_CLASS_LIST){
            load(nClass);
        }

    }

    /**
     * 加载类型
     * @param loadClass
     */
    public static Map<String,Class<?>> load(Class<?> loadClass)  {
        log.info("记载类型为{}的SPI",loadClass.getName());
        //路径扫描，用户自定义的SPI高于系统SPI
        Map<String,Class<?>>keyClassMap=new HashMap<>();
        for (String scanDir:SCAN_DIR) {
            log.info("扫描路径为{}",scanDir + loadClass.getName());
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = null;
            try {
                resources = contextClassLoader.getResources(scanDir + loadClass.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(resources.hasMoreElements()){
                try {
                    URL resource=resources.nextElement();
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        String[] split = line.split("=");
                        keyClassMap.put(split[0],Class.forName(split[1]));
                    }
                } catch (Exception e) {
                    log.info("SPI resource load fail",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return  keyClassMap;
    }

    /**
     * 获取接口实例
     * @param tClass
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<?> tClass,String key){
        String className = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);
        if(keyClassMap==null){
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型",className));
        }else if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader %s 不存在 %s 类型",className,key));
        }
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName,implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("%s 实例化失败",implClassName));
            }
        }
        return (T)instanceCache.get(implClassName);
    }

    public static void main(String[] args) {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "kryo");
        System.out.println(serializer);
    }

}
