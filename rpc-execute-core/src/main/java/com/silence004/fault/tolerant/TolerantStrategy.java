package com.silence004.fault.tolerant;

import com.silence004.response.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    /**
     * 容错
     * @param context 上下文，传递数据
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String,Object> context, Exception e);

}
