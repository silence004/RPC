package com.silence004.config;

import com.silence004.serialize.Serializer;
import com.silence004.serialize.constant.SerislizerKeys;
import lombok.Data;

/**
 * Rpc全局框架配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name="Rpc";

    /**
     *版本
     */
    private String version="1.0";

    /**
     * 服务器主机名
     */
    private String serveHost="localhost";

    /**
     * 服务器端口号
     */
    private Integer servePort=8080;

    /**
     * 模拟调用
     */
    private Boolean mock=false;

    /**
     * 序列化器
     */
    private String serializer= SerislizerKeys.JDK;

    /**
     * 注册中心
     */
    private RegistryConfig registryConfig= new RegistryConfig();
}
