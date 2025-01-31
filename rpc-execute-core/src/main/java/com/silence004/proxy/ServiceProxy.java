package com.silence004.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.silence004.RpcApplication;
import com.silence004.config.RpcConfig;
import com.silence004.constant.RpcConstant;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import com.silence004.serialize.Serializer;
import com.silence004.serialize.SerializerFactory;
import com.silence004.model.ServiceMetaInfo;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Serializer serializer = SerializerFactory.getInstance(rpcConfig.getSerializer());

        String serviceName = method.getDeclaringClass().getName();

        RpcRequest rpcRequest = RpcRequest.builder()
                .ServiceName(serviceName)
                .parametersTypes(method.getParameterTypes())
                .methodName(method.getName())
                .args(args)
                .build();



        try {
            byte[] bytes = serializer.serialize(rpcRequest);

            //从注册中心获取服务请求地址
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo selectedserviceMetaInfo = serviceMetaInfoList.get(0);


            try(HttpResponse response = HttpRequest.post("http://"+rpcConfig.getServeHost()+":"+rpcConfig.getServePort())
                        .body(bytes)
                        .execute()) {
                RpcResponse rpcResponse = serializer.deSerialize(response.bodyBytes(), RpcResponse.class);
                return rpcResponse.getData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
