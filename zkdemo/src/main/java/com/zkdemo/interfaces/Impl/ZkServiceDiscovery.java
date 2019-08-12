package com.zkdemo.interfaces.Impl;

import com.zkdemo.interfaces.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ZkServiceDiscovery implements ServiceDiscovery {
    private String zkAddress = "127.0.0.1:2182";

    private final List<String> addressCache = new CopyOnWriteArrayList<>();

    private ZkClient zkClient;

    public void init() {

        //创建连接
        zkClient = new ZkClient(zkAddress,
                ZkServiceRegistryImpl.ZK_SESSION_TIMEOUT,
                ZkServiceRegistryImpl.ZK_CONNECTION_TIMEOUT);
        System.out.println(">>> connect to zookeeper zkAddress==》 "+zkAddress);

    }

    @Override
    public  String discover(String name) {

        try {
            //服务节点路径
            String servicePath = ZkServiceRegistryImpl.ZK_REGISTRY + "/" + name;

            //获取服务节点
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format(">>>can't find any service node on path {}", servicePath));
            }

            //从本地缓存获取某个服务地址
            String address;
            int addressCacheSize = addressCache.size();
            if (addressCacheSize > 0) {
                //本地缓存有直接本地返回服务
                if (addressCacheSize == 1) {
                    //只有一个服务
                    address = addressCache.get(0);
                    System.out.println(">>>get 唯一的 address node:" + address);
                } else {
                    //发现多个服务，随机算法，返回一个
                    address = addressCache.get(ThreadLocalRandom.current().nextInt(addressCacheSize));
                    System.out.println(">>>get 随机算法的 address node:" + address);
                }


                //从zk服务注册中心获取某个服务地址
            } else {
                //本地缓存没有，请求zk 获取节点下的服务地址
                System.out.println("servicePath===>"+servicePath);
                List<String> addressList = zkClient.getChildren(servicePath);
                //添加到缓存中
                addressCache.addAll(addressList);

                //监听servicePath下的子文件是否发生变化
                zkClient.subscribeChildChanges(servicePath, (parentPath, currentChilds) -> {
                    System.out.println(">>>servicePath is changed:" + parentPath);
                    //监听到节点路径下服务有变化，则清空缓存，
                    addressCache.clear();
                    // 添加最新的节点下服务地址
                    addressCache.addAll(currentChilds);

                });

                if (CollectionUtils.isEmpty(addressList)) {
                    throw new RuntimeException(String.format(">>>can't find any address node on path {}", servicePath));
                }
                int nodeSize = addressList.size();
                if (nodeSize == 1) {
                    //只有一个服务
                    address = addressList.get(0);
                } else {

                    //发现多个服务，随机算法，返回一个
                    address = addressList.get(ThreadLocalRandom.current().nextInt(nodeSize));
                }
                System.out.println(">>>get address node:" + address);

            }

            //获取IP和端口号
            String addressPath = servicePath + "/" + address;
            String hostAndPort = zkClient.readData(addressPath);
            System.out.println("服务地址；"+hostAndPort);
            return hostAndPort;

        } catch (Exception e) {
            System.out.println(">>> service discovery exception: " + e.getMessage());
            zkClient.close();
        }

        return null;

    }

}
