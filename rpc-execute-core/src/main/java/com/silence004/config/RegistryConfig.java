package com.silence004.config;

import lombok.Data;

@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry="etcd";

    /**
     * 注册中心地址
     */
    private String address="http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间(ms)
     */
    private Long timeout=10000L;

}
