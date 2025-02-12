package com.silence004.rpcspringbootstarter.annotation;

import com.silence004.constant.RpcConstant;
import com.silence004.fault.retry.constant.RetryStrategyKeys;
import com.silence004.fault.tolerant.TolerantStrategyKeys;
import com.silence004.loadbalancer.constant.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费者注解，用于注入服务
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     * @return
     */
    String serviceVersion()default RpcConstant.DEFAULT_SERVE_VERSION;

    /**
     * 负载均衡器
     */
    String loadBalancer()default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    String retryStrategy()default RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    String tolerantStrategy()default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     */
    boolean mock()default false;
}
