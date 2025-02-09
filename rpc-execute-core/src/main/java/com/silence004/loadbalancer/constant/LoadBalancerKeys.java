package com.silence004.loadbalancer.constant;

public interface LoadBalancerKeys {
    /**
     * 轮询
     */
    String ROUND_ROBIN="roundRobin";

    /**
     * 随机
     */
    String RANDOM="random";

    /**
     * 一致性哈希
     */
    String CONSISTENT_HASH="consistentHash";


}
