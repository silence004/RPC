package com.silence004.register.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.silence004.config.RegistryConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.register.Registry;
import com.silence004.register.RegistryServiceCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZooKeeperRegistry implements Registry {
    private CuratorFramework client;

    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     *根节点
     */
    private static final String ZK_ROOT_PATH="/rpc/zk" ;

    /**
     *本机注册的节点key缓存，用以维护续期
     */
    private final Set<String> localRegistryNodeKeySet=new HashSet<>();

    /**
     *注册中心服务本地缓存
     */
    private final RegistryServiceCache registryServiceCache=new RegistryServiceCache();


    /**
     *正在监听的key集合
     */
    private final Set<String>watchingkeySet=new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
       client=CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()),3))
                .build();

       serviceDiscovery=ServiceDiscoveryBuilder
                .builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //注册到zk
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));

        //本地缓存注册key
        String registerKey=ZK_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegistry(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //从本地缓存移除
        String registerKey=ZK_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        localRegistryNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if(cacheServiceMetaInfoList!=null){
            return cacheServiceMetaInfoList;
        }

        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstanceList = serviceDiscovery.queryForInstances(serviceKey);

            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = serviceInstanceList.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());

            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String key:localRegistryNodeKeySet) {
            try {
                client.delete().guaranteed().forPath(key);
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败");
            }
        }
        if (client!=null){
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //zookeeper建立的是临时节点，服务器故障会自动丢失临时节点
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey=ZK_ROOT_PATH+"/"+serviceNodeKey;
        boolean newWatch = watchingkeySet.add(watchKey);
        if(newWatch){
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData ->  registryServiceCache.clearCache())
                            .forChanges((oldNode,node)->registryServiceCache.clearCache())
                            .build()
            );
        }

    }

    /**
     * 创建服务实例
     * @param serviceMetaInfo
     * @return
     */
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress=serviceMetaInfo.getServiceHost()+":"+serviceMetaInfo.getServiceAddress();

        try {
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
