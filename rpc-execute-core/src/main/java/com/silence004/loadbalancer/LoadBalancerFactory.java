package com.silence004.loadbalancer;

import com.silence004.loadbalancer.impl.RoundRobinLoadBalancer;
import com.silence004.spi.SpiLoader;

public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    private static final LoadBalancer DEFAULT_LOAD_BALANCER=new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }
}
