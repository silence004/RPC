package com.silence004.fault.retry.impl;

import com.silence004.fault.retry.RetryStrategy;
import com.silence004.response.RpcResponse;

import java.util.concurrent.Callable;

public class NoRetryStrategy implements RetryStrategy {

    /**
     * 不重试
     * @param callable
     * @return
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
