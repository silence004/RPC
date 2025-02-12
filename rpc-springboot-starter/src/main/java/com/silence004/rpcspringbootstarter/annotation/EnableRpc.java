package com.silence004.rpcspringbootstarter.annotation;

import com.silence004.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.silence004.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.silence004.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 是否启动rpc
     */
    boolean IsServer()default true;
}
