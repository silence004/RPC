package com.silence004.rpcspringbootstarter.bootstrap;

import com.silence004.rpcspringbootstarter.annotation.RpcService;
import com.silence004.RpcApplication;
import com.silence004.config.RegistryConfig;
import com.silence004.config.RpcConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.register.LocalRegistry;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * bean初始化后执行，注册服务
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if(rpcService!=null){
            //需要注册服务
            //1.获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            //默认值处理
            if(interfaceClass==void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName=interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            //2.本地注册
            LocalRegistry.register(serviceName,beanClass);

            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServeHost());
            serviceMetaInfo.setSerivePort(Integer.valueOf(rpcConfig.getServePort()));

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean,beanName);
    }
}
