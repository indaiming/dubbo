package com.userserviceprovider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmallinterface.bean.Order;
import com.gmallinterface.service.OrderDemoService;
import org.springframework.beans.factory.annotation.Value;

/**
 * 最小活跃数的负责均衡策略
 */
@Service(loadbalance = "leastactive",filter = "activelimit")
public class OrderServiceImpl implements OrderDemoService {
    @Value("${server.port}")
    private String port;
    @Override
    public Order getOrder() {
        Order order = new Order();
        order.setGoodsName("鞋子");
        order.setGoodsNo("XZX53542333111");
        order.setId(111);
        return order;
    }
}
