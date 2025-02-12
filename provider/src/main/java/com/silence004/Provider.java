package com.silence004;

import com.silence004.bootstrap.ProviderBootstrap;
import com.silence004.config.RpcConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.model.ServiceRegisterInfo;
import com.silence004.register.LocalRegistry;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import com.silence004.service.HttpServer;
import com.silence004.service.UserService;
import com.silence004.service.UserServiceImpl;
import com.silence004.service.http.impl.VertxHttpServer;
import com.silence004.service.tcp.VertxTcpClient;
import com.silence004.service.tcp.impl.VertxTcpServe;

import java.util.ArrayList;
import java.util.List;

public class Provider {

    public static void main(String[] args) {

//        RpcApplication.init();
//
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName,UserServiceImpl.class);
//
//        RpcConfig rpcConfig=RpcApplication.getRpcConfig();
//        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
//        serviceMetaInfo.setServiceHost(rpcConfig.getServeHost());
//        serviceMetaInfo.setSerivePort(rpcConfig.getServePort());
//        try {
//            registry.register(serviceMetaInfo);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        //启动web服务
////        HttpServer httpServer = new VertxHttpServer();
////        //监听全局配置中的端口
////        httpServer.doStart(rpcConfig.getServePort());
//
//        //启动tcp服务
//        VertxTcpServe vertxTcpServe = new VertxTcpServe();
//        vertxTcpServe.doStart(rpcConfig.getServePort()==null?8081:rpcConfig.getServePort());

        List<ServiceRegisterInfo<?>>serviceRegisterInfoList=new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo=new ServiceRegisterInfo(UserService.class.getName(),UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        //服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);



    }

}
