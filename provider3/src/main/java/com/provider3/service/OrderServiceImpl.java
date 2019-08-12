package com.provider3.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmallinterface.bean.Order;
import com.gmallinterface.service.OrderDemoService;
import org.springframework.beans.factory.annotation.Value;

@Service
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
