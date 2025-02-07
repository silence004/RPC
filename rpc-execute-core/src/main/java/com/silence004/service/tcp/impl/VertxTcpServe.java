package com.silence004.service.tcp.impl;

import com.silence004.service.HttpServer;
import com.silence004.service.tcp.TcpServerHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

import java.nio.charset.StandardCharsets;

public class VertxTcpServe implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建vertx实例
        Vertx vertx = Vertx.vertx();

        //创建tcp服务器
        NetServer server = vertx.createNetServer();

//        //处理连接请求
//        server.connectHandler(netSocket -> {
////                if(buffer.getBytes().length<messageLength){
////                    System.out.println("半包，length="+buffer.getBytes().length);
////                    return;
////                }
////                if(buffer.getBytes().length>messageLength){
////                    System.out.println("粘包，length="+buffer.getBytes().length);
////                    return;
////                }
//                //构建parser，遇到半包就留到下次读取
//
//               //读取请求头
//                RecordParser parser = RecordParser.newFixed(8);
//                parser.setOutput(new Handler<Buffer>() {
//
//                    int size=-1;
//                    Buffer resultBuffer = Buffer.buffer();
//
//                    @Override
//                    public void handle(Buffer buffer) {
//                        if(size==-1){
//                            size= buffer.getInt(4);
//                            parser.fixedSizeMode(size);
//                            resultBuffer.appendBuffer(buffer);
//                        }else{
//                            resultBuffer.appendBuffer(buffer);
//                            System.out.println(resultBuffer.toString());
//                            size=-1;
//                            parser.fixedSizeMode(8);
//                            resultBuffer=Buffer.buffer();
//                        }
//                    }
//                });
//                netSocket.handler(parser);
//        });

        server.connectHandler(new TcpServerHandler());

        //监听端口
        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("TCP serve started on port:"+port);
            }else{
                System.out.println("TCP serve fail :"+result.cause());
            }
        });

    }

    public static void main(String[] args) {
        new VertxTcpServe().doStart(8888);
    }
}
