package com.silence004.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.silence004.RpcApplication;
import com.silence004.config.RpcConfig;
import com.silence004.constant.RpcConstant;
import com.silence004.protocol.Enum.ProtocolMessageSerializerEnum;
import com.silence004.protocol.Enum.ProtocolMessageStatusEnum;
import com.silence004.protocol.Enum.ProtocolMessageTypeEnum;
import com.silence004.protocol.ProtocolMessage;
import com.silence004.protocol.ProtocolMessageDecoder;
import com.silence004.protocol.ProtocolMessageEncoder;
import com.silence004.protocol.constant.ProtocolConstant;
import com.silence004.register.Registry;
import com.silence004.register.RegistryFactory;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import com.silence004.serialize.Serializer;
import com.silence004.serialize.SerializerFactory;
import com.silence004.model.ServiceMetaInfo;

import com.silence004.service.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//JDK动态代理
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
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion()==null?RpcConstant.DEFAULT_SERVE_VERSION:rpcConfig.getVersion());
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo selectedserviceMetaInfo = serviceMetaInfoList.get(0);

            RpcResponse response = VertxTcpClient.doRequest(rpcRequest, selectedserviceMetaInfo);
            return response.getData();
        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }
    }
}





//http连接逻辑
//public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//    RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//    Serializer serializer = SerializerFactory.getInstance(rpcConfig.getSerializer());
//
//    String serviceName = method.getDeclaringClass().getName();
//
//    RpcRequest rpcRequest = RpcRequest.builder()
//            .ServiceName(serviceName)
//            .parametersTypes(method.getParameterTypes())
//            .methodName(method.getName())
//            .args(args)
//            .build();
//
//
//    try {
//        byte[] bytes = serializer.serialize(rpcRequest);
//
//        //从注册中心获取服务请求地址
//        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVE_VERSION);
//        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
//        if (CollUtil.isEmpty(serviceMetaInfoList)) {
//            throw new RuntimeException("暂无服务地址");
//        }
//        ServiceMetaInfo selectedserviceMetaInfo = serviceMetaInfoList.get(0);
//
//            try(HttpResponse response = HttpRequest.post("http://"+rpcConfig.getServeHost()+":"+rpcConfig.getServePort())
//                        .body(bytes)
//                        .execute()) {
//                RpcResponse rpcResponse = serializer.deSerialize(response.bodyBytes(), RpcResponse.class);
//                return rpcResponse.getData();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
