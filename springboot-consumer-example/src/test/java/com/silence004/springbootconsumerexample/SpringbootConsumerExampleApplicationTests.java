package com.silence004.springbootconsumerexample;


import com.silence004.springbootconsumerexample.service.UserServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootConsumerExampleApplicationTests {
    @Resource
    private UserServiceImpl userService;

    @Test
    void contextLoads() {
        userService.test();
    }

}
