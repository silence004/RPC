package com.silence004.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.silence004.service.UserService;
import com.silence004.utils.request.RpcRequest;
import com.silence004.utils.response.RpcResponse;
import com.silence004.utils.serialize.impl.JdkSerializer;
import com.silence004.vo.User;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;


public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JdkSerializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .ServiceName(method.getDeclaringClass().getName())
                .parametersTypes(method.getParameterTypes())
                .methodName(method.getName())
                .args(args)
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);

            try(HttpResponse response = HttpRequest.post("http://localhost:8080")
                        .body(bytes)
                        .execute()) {
                RpcResponse rpcResponse = serializer.deSerialize(response.bodyBytes(), RpcResponse.class);
                return (User) rpcResponse.getData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
