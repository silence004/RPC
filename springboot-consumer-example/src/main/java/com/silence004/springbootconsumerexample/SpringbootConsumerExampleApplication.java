package com.silence004.springbootconsumerexample;

import com.silence004.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(IsServer = false)
public class SpringbootConsumerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootConsumerExampleApplication.class, args);
    }

}
