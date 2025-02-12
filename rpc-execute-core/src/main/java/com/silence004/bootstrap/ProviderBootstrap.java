package com.silence004.bootstrap;

import com.silence004.RpcApplication;
import com.silence004.config.RegistryConfig;
import com.silence004.config.RpcConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.model.ServiceRegisterInfo;
import com.silence004.register.LocalRegistry;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import com.silence004.service.tcp.impl.VertxTcpServe;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        //rpc框架初始化，配置和注册中心
        RpcApplication.init();
        //获取全局配置
        final RpcConfig rpcConfig=RpcApplication.getRpcConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo: serviceRegisterInfoList) {
            String serviceName=serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册到服务中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            serviceMetaInfo.setServiceHost(rpcConfig.getServeHost());
            serviceMetaInfo.setSerivePort(rpcConfig.getServePort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }

        }
        //启动tcp服务
        VertxTcpServe vertxTcpServe = new VertxTcpServe();
        vertxTcpServe.doStart(rpcConfig.getServePort()==null?8081:rpcConfig.getServePort());
    }
}
