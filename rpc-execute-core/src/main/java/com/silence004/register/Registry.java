package com.silence004.register;

import com.silence004.config.RegistryConfig;
import com.silence004.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    /**
     * 初始化
     * @param registryConfig
     */
   void init(RegistryConfig registryConfig);

    /**
     * 注册服务(服务端)
     * @param serviceMetaInfo
     */
   void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务注销(服务端)
     * @param serviceMetaInfo
     */
    void unRegistry(ServiceMetaInfo serviceMetaInfo) ;

    /**
     * 服务发现(消费端获取所有服务节点)
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测(服务端)
     */
    void heartBeat();

    /**
     * 监听(消费端)
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);



}
