package com.silence004;

import com.silence004.service.http.impl.VertxHttpServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebRpcApplication {

    public static void main(String[] args) {

//        SpringApplication.run(WebRpcApplication.class, args);
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }

}
