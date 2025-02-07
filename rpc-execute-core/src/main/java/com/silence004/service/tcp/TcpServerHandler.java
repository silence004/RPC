package com.silence004.service.tcp;

import com.silence004.protocol.Enum.ProtocolMessageStatusEnum;
import com.silence004.protocol.Enum.ProtocolMessageTypeEnum;
import com.silence004.protocol.ProtocolMessage;
import com.silence004.protocol.ProtocolMessageDecoder;
import com.silence004.protocol.ProtocolMessageEncoder;
import com.silence004.register.LocalRegistry;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        /**
         * 请求处理逻辑
         */
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer->{

        ProtocolMessage<RpcRequest> protocolMessage;

            try {
                protocolMessage= (ProtocolMessage<RpcRequest>)ProtocolMessageDecoder.Decoder(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }

            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse response = new RpcResponse();
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParametersTypes());
                Object result = method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                //封装返回类型
                response.setData(result);
                response.setDataType(method.getReturnType());
                response.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setException(e);
            }

            ProtocolMessage.Header header=protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus( (byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header,response);
            System.out.println(responseProtocolMessage);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (Exception e) {
                throw new RuntimeException("协议消息编码错误");
            }

        });

        netSocket.handler(tcpBufferHandlerWrapper);

    }
}
