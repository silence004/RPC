package com.silence004.config;

import com.silence004.fault.retry.constant.RetryStrategyKeys;
import com.silence004.fault.tolerant.TolerantStrategy;
import com.silence004.fault.tolerant.TolerantStrategyKeys;
import com.silence004.loadbalancer.LoadBalancer;
import com.silence004.loadbalancer.constant.LoadBalancerKeys;
import com.silence004.loadbalancer.impl.RoundRobinLoadBalancer;
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

    /**
     * 负载均衡器
     */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy= RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy= TolerantStrategyKeys.FAIL_FAST;
}
