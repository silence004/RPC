package com.silence004.serialize.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import com.silence004.serialize.Serializer;

import java.io.IOException;


public class JsonSerializer implements Serializer {
    private static final  ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        System.out.println("json serializer is used");
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> classType) throws IOException {
        System.out.println("json serializer is used");
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if(obj instanceof RpcRequest){
            return handleRequest((RpcRequest)obj,classType);
        }

        if(obj instanceof RpcResponse){
            return handleResponse((RpcResponse)obj,classType);
        }
        return null;
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> classType) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes,rpcResponse.getDataType()));
        return classType.cast(rpcResponse);
    }

    /**
     * object原始对象会被擦除，反序列化时会被作为linkedhashmap，需要做处理
     * @param rpcRequest
     * @param classType
     * @param <T>
     * @return
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> classType) throws IOException {
        Object[] args = rpcRequest.getArgs();
        Class<?>[] parametersTypes = rpcRequest.getParametersTypes();
        for (int i=0;i<parametersTypes.length;i++) {
            Class<?> clazz = parametersTypes[i];
            if(clazz.isAssignableFrom(args[i].getClass())){
                byte[] argbytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argbytes, parametersTypes[i]);
            }
        }
        return classType.cast(rpcRequest);
    }
}
