package com.silence004.utils.http;

import com.silence004.register.LocalRegistry;
import com.silence004.utils.request.RpcRequest;
import com.silence004.utils.response.RpcResponse;
import com.silence004.utils.serialize.Serializer;
import com.silence004.utils.serialize.impl.JdkSerializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 直接在服务端调用反射获取结果，不使用代理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        final Serializer serializer = new JdkSerializer();

        System.out.println("receive request:" + httpServerRequest.method()+" "+httpServerRequest.uri());

        //异步处理http请求
        httpServerRequest.bodyHandler(body ->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest=null;

            try {
               rpcRequest=serializer.deSerialize(bytes,RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse=new RpcResponse();
            if(rpcRequest==null){
                rpcResponse.setMessage("rpcRequest is null");
                System.out.println(rpcRequest.getMethodName()+" "+rpcRequest.getServiceName());
                doResponse(httpServerRequest,rpcResponse,serializer);
                return;
            }

            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParametersTypes());
                Object result = method.invoke(implClass.newInstance(),rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(httpServerRequest,rpcResponse,serializer);

        });


    }

    /**
     * 响应
     * @param httpServerRequest
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse Response = httpServerRequest.response()
                .putHeader("content-type", "application/json");

        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            Response.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            Response.end(Buffer.buffer());
        }
    }

}
