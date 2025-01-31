package com.silence004.test;

import com.silence004.config.RegistryConfig;
import com.silence004.model.ServiceMetaInfo;
import com.silence004.register.Registry;
import com.silence004.register.impl.EtcdRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest {
  final Registry registry=new EtcdRegister();

  @Before
  public void init(){
    RegistryConfig registryConfig = new RegistryConfig();
    registry.init(registryConfig) ;
  }

  @Test
  public void Register() throws Exception {
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("silence004`service");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServiceVersion("1.0");
    serviceMetaInfo.setSerivePort(1234);
    registry.register(serviceMetaInfo);
    serviceMetaInfo=new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("silence004`service");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServiceVersion("2.0");
    serviceMetaInfo.setSerivePort(1235);
    registry.register(serviceMetaInfo);
  }

  @Test
  public void unRegister(){
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("silence004`service1");
    serviceMetaInfo.setServiceHost("localhost");
    serviceMetaInfo.setServiceVersion("1.0");
    serviceMetaInfo.setSerivePort(1234);
    registry.unRegistry(serviceMetaInfo);
  }

  @Test
  public void serviceDiscovery(){
    ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
    serviceMetaInfo.setServiceName("silence004`service");
    serviceMetaInfo.setServiceVersion("2.0");
    String serviceKey = serviceMetaInfo.getServiceKey();
    List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
    System.out.println(serviceMetaInfoList.get(0));
    Assert.assertNotNull(serviceMetaInfoList);
  }

  @Test
  public void heartBeat() throws Exception{
    Register();
    Thread.sleep(120 * 1000L);


  }
}
