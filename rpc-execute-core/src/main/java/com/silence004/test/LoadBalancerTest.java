package com.silence004.test;

import com.silence004.loadbalancer.LoadBalancer;
import com.silence004.loadbalancer.impl.ConsistentHashLoadBalancer;
import com.silence004.loadbalancer.impl.RandomLoadBalancer;
import com.silence004.loadbalancer.impl.RoundRobinLoadBalancer;
import com.silence004.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadBalancerTest {
    final LoadBalancer loadBalancer=new ConsistentHashLoadBalancer();

    @Test
    public void select(){
        Map<String,Object>requestParams=new HashMap<>();
        requestParams.put("method","apple");
        //服务列表
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("Myservice");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setSerivePort(1234);

        ServiceMetaInfo serviceMetaInfo2=new ServiceMetaInfo();
        serviceMetaInfo2.setServiceName("Myservice");
        serviceMetaInfo2.setServiceVersion("1.0");
        serviceMetaInfo2.setServiceHost("silence");
        serviceMetaInfo2.setSerivePort(80);
        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo, serviceMetaInfo2);

        ServiceMetaInfo service = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(service);
        Assert.assertNotNull(service);
        service = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(service);
        Assert.assertNotNull(service);
        service = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(service);
        Assert.assertNotNull(service);



    }
}
