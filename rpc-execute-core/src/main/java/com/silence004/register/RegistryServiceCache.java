package com.silence004.register;

import com.silence004.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param newServiceCache
     */
    public void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache=newServiceCache;
    }

    /**
     * 读缓存
     * @return
     */
    public List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    public void clearCache(){
        this.serviceCache=null;
    }
}
