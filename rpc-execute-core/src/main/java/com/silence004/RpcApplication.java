package com.silence004;


import com.silence004.config.RegistryConfig;
import com.silence004.config.RpcConfig;
import com.silence004.constant.RpcConstant;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import com.silence004.util.ConfigUtil;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RpcApplication {

    private static volatile RpcConfig rpcConfig;
    private static final Logger log = LoggerFactory.getLogger(RpcApplication.class);


    /**
     * 框架初始化，自定义配置
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig=newRpcConfig;
        log.info("rpc init config={}",newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init config={}",newRpcConfig.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化，读取配置文件
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig=ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFRIX);
        } catch (Exception e) {
            e.printStackTrace();
            newRpcConfig=new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 双重校验锁，懒加载
     * @return
     */
    public static RpcConfig getRpcConfig(){
        if(rpcConfig ==null){
            synchronized(RpcApplication.class){
                if(rpcConfig ==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
