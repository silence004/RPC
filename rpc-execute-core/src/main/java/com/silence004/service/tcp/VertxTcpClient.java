package com.silence004.service.tcp;

import cn.hutool.core.util.IdUtil;
import com.silence004.RpcApplication;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.protocol.Enum.ProtocolMessageSerializerEnum;
import com.silence004.protocol.Enum.ProtocolMessageTypeEnum;
import com.silence004.protocol.ProtocolMessage;
import com.silence004.protocol.ProtocolMessageDecoder;
import com.silence004.protocol.ProtocolMessageEncoder;
import com.silence004.protocol.constant.ProtocolConstant;
import com.silence004.request.RpcRequest;
import com.silence004.response.RpcResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class VertxTcpClient {

    /**
     * 发送请求
     */
    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost", result->{
            if(result.succeeded()){
                System.out.println("Connect to Tcp server");
                NetSocket socket = result.result();
                for(int i=0;i<1000;i++){
                    Buffer buffer =Buffer.buffer();
                    String str="Hellxxx,server!Hello,server!Hello,server!Hello,server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                //接收响应
                socket.handler(buffer -> {
                    System.out.println("Receive response from server:"+buffer.toString());
                });
            }else {
                System.out.println("Failed to connect to TCP server");
            }

        });
    }

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //发送tcp请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getSerivePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Connect to TCP serve");
                        NetSocket socket = result.result();
                        //构建数据发送
                        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                        ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                        //生成全局请求ID
                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);

                        //编码请求
                        try {
                            Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
                            socket.write(encode);
                        } catch (Exception e) {
                            throw new RuntimeException("协议消息编码错误");
                        }

                        //接收响应
                        TcpBufferHandlerWrapper tcpBufferHandlerWrapper=new TcpBufferHandlerWrapper(buffer -> {
                            try {
                                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.Decoder(buffer);
                                System.out.println(rpcResponseProtocolMessage);
                                responseFuture.complete(rpcResponseProtocolMessage.getBody());
                            } catch (Exception e) {
                                throw new RuntimeException("协议消息解码错误");
                            }
                        });
                        socket.handler(tcpBufferHandlerWrapper);
                    } else {
                        System.out.println("Failed to connect to TCP server");
                        return ;
                    }

                });
        //阻塞线程，直到获取到异步结果
        RpcResponse response = responseFuture.get();
        netClient.close();
        return response;
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
