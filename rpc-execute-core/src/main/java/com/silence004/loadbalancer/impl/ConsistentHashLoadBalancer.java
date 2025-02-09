package com.silence004.loadbalancer.impl;

import com.silence004.loadbalancer.LoadBalancer;
import com.silence004.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性哈希环，存放虚拟节点
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes=new TreeMap<>();

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_NUMBER=100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        //构建虚拟节点
        for (ServiceMetaInfo serviceMetaInfo: serviceMetaInfoList) {
            for(int i=0;i<VIRTUAL_NODE_NUMBER;i++){
                int Hash=getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                virtualNodes.put(Hash,serviceMetaInfo);
            }
        }

        int hash=getHash(requestParams);

        Map.Entry<Integer,ServiceMetaInfo> entry=virtualNodes.ceilingEntry(hash);
        if(entry==null){
            //如果没有大于等于调用请求hash值的虚拟节点，返回首节点
            entry=virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    private int getHash(Object key) {
       return key.hashCode();
    }
}
