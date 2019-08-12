package com.zkdemo.interfaces.Impl;

import com.zkdemo.interfaces.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;

/**
 * 注册服务
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {
    private String zkAddress = "127.0.0.1:2182";
    public static final int ZK_SESSION_TIMEOUT = 50000;
    public static final int ZK_CONNECTION_TIMEOUT = 10000;
    public static final String ZK_REGISTRY = "/zkdemo";
    private ZkClient zkClient;

    public void init() {
        //创建zk连接
        zkClient = new ZkClient(zkAddress, ZkServiceRegistryImpl.ZK_SESSION_TIMEOUT, ZkServiceRegistryImpl.ZK_CONNECTION_TIMEOUT);
        System.out.println(">>> connect to zookeeper:==>" + zkAddress);

    }

    @Override
    public void registry(String serviceName,String serviceAddress) {
        //创建registry节点（持久）
        String registryPath = ZkServiceRegistryImpl.ZK_REGISTRY;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            System.out.println(">>>创建最外层节点 create registry node:" + registryPath);
        }

        //创建service节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            System.out.println(">>>持久节点 create service node:" + servicePath);
        }

        //创建address节点（临时）,并写入 地址值（传送的数据）
        //模拟十条数据写入 地址值
        for (int i = 0; i <10 ; i++) {
            String addressPath = servicePath +"/address"+i+"-";
            String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress+i);
            System.out.println(">>>临时节点 create address node:" + addressNode);

        }

    }
}
