package com.silence004.springbootproviderexample;

import com.silence004.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class SpringbootProviderExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootProviderExampleApplication.class, args);
    }

}
