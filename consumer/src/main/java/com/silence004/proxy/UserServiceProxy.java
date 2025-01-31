package com.silence004.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.silence004.service.UserService;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import com.silence004.serialize.impl.JdkSerializer;
import com.silence004.vo.User;



import java.io.IOException;

//静态代理，在消费者端封装rpc请求，通过http请求向消费者端获取执行结果
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
       final JdkSerializer Serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .ServiceName(UserService.class.getName())
                .methodName("getUser")
                .parametersTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = Serializer.serialize(rpcRequest);

            try(HttpResponse httpResponse = HttpRequest
                        .post("http://localhost:8080")
                        .body(bodyBytes)
                        .execute()) {
                byte[] bytes = httpResponse.bodyBytes();
                RpcResponse rpcResponse = Serializer.deSerialize(bytes, RpcResponse.class);
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
