package com.silence004.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 请求的服务名称
     */
   private String ServiceName;

    /**
     * 请求的方法名称
     */
   private String methodName;

    /**
     * 请求的参数类型列表
     */
   private Class<?>[] parametersTypes;

    /**
     * 请求的参数列表
     */
   private Object[] args;
}
