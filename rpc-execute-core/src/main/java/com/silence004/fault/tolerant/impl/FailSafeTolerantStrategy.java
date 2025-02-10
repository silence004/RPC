package com.silence004.fault.tolerant.impl;

import com.silence004.fault.tolerant.TolerantStrategy;
import com.silence004.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("Fail-Fast处理异常",e);
        return new RpcResponse();
    }
}
