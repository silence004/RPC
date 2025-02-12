package com.silence004.rpcspringbootstarter.bootstrap;

import com.silence004.rpcspringbootstarter.annotation.EnableRpc;
import com.silence004.RpcApplication;
import com.silence004.config.RpcConfig;
import com.silence004.service.tcp.impl.VertxTcpServe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * spring初始化时执行rpc初始化
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean isServer = (boolean)importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("IsServer");
        //框架初始化
        RpcApplication.init();

        //获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //启动服务
        if(isServer){
            VertxTcpServe vertxTcpServe = new VertxTcpServe();
            vertxTcpServe.doStart(rpcConfig.getServePort()==null?8081:rpcConfig.getServePort());
        }else {
            log.info("不启动server");
        }

    }
}
