package com.silence004.register.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.silence004.config.RegistryConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.register.Registry;
import com.silence004.register.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegister implements Registry {
    private Client client;
    private KV kvClient;

    /**
     * 本机注册节点key集合，维护续期
     */
    private final Set<String> localRegistryNodeKeySet=new HashSet<>();

    /**
     * 根节点
     */
    private final String ETDC_ROOT_PATH="/rpc/";

    /**
     * 注册中心服务本地缓存
     */
    private final RegistryServiceCache registryServiceCache=new RegistryServiceCache();

    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeySet=new ConcurrentHashSet<>();



    @Override
    public void init(RegistryConfig registryConfig) {
       client= Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
       kvClient=client.getKVClient();
       heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{
        Lease leaseClient = client.getLeaseClient();
        //创建30s的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置需要存储的键值对
        String registryKey=ETDC_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //联系键值对和租约，设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();

        //添加注册的key到租约集合
        localRegistryNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegistry(ServiceMetaInfo serviceMetaInfo) {
        String registryKey=ETDC_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registryKey,StandardCharsets.UTF_8));

        //从本地注册缓存中移除key
        localRegistryNodeKeySet.remove(registryKey);

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrifix=ETDC_ROOT_PATH+serviceKey+"/";
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if(cacheServiceMetaInfoList!=null){
            return cacheServiceMetaInfoList;
        }

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrifix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream()
                    .map(kv -> {
                                String key=kv.getKey().toString();
                                watch(key);
                                String value = kv.getValue().toString();
                                return JSONUtil.toBean(value, ServiceMetaInfo.class);
                            }
                    ).collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;

        } catch (Exception e) {
           throw new RuntimeException("获取服务列表失败",e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        for (String key: localRegistryNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败");
            }
        }
        if(kvClient!=null){
            kvClient.close();
        }
        if(client!=null){
            client.close();
        }

    }

    @Override
    public void heartBeat() {
        //每十秒续签一次
       CronUtil.schedule("*/10 * * * * *",new Task(){
           @Override
           public void execute() {
               for (String key :localRegistryNodeKeySet) {
                   List<KeyValue> kvs = null;
                   try {
                       kvs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                               .get()
                               .getKvs();
                       //节点过期，重启节点才能重新注册
                       if(CollUtil.isEmpty(kvs)){
                           continue;
                       }
                       KeyValue keyValue = kvs.get(0);
                       String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                       ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                       register(serviceMetaInfo);
                       System.out.println("续签成功");
                   } catch (Exception e) {
                       throw new RuntimeException(key+"续签失败",e);
                   }
               }
           }
       });
       CronUtil.setMatchSecond(true);
       CronUtil.start();

    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if(newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),watchResponse -> {
                for (WatchEvent event:
                     watchResponse.getEvents()) {
                    switch (event.getEventType()){
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }

                }
            });

        }
    }
}
