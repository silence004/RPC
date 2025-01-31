package com.silence004.model;

import cn.hutool.core.util.StrUtil;
import com.silence004.RpcApplication;
import com.silence004.constant.RpcConstant;
import lombok.Data;

@Data
public class ServiceMetaInfo {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 版本号
     */
    private String serviceVersion= RpcConstant.DEFAULT_SERVE_VERSION;
    /**
     *服务主机
     */
    private String serviceHost;
    /**
     * 服务端口
     */
    private Integer serivePort;
    /**
     * 服务分组
     */
    private String serviceGroup="default";

    /**
     * 获取服务键名
     */
    public String getServiceKey(){
        return String.format("%s:%s",serviceName,serviceVersion);
    }
    /**
     * 获取服务注册节点键名
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s:%s",getServiceKey(),serviceHost,serivePort);
    }
    /**
     * 获取完整服务注册地址
     */
    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",getServiceNodeKey(),serviceHost,serivePort);
        }
        return String.format("/%s:%s",serviceHost,serivePort);
    }

}
