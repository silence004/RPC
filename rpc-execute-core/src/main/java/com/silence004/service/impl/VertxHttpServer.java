package com.silence004.service.impl;

import com.silence004.service.HttpServer;
import com.silence004.http.HttpServerHandler;
import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
//        server.requestHandler(request->{
//            System.out.println("received request: "+request.method()+" "+request.uri());
//            request.response().putHeader("content-type","text/plain").end("hello vertx.x http server");
//        });

        server.requestHandler(new HttpServerHandler());

        server.listen(port,result -> {
            if(result.succeeded()){
                System.out.println("server is now listen port :"+port);
            }else {
                System.out.println("failed to start server: "+result.cause());
            }
        });
    }
}
