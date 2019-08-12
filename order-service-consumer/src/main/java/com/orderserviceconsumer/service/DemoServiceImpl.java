package com.orderserviceconsumer.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmallinterface.bean.Order;
import com.gmallinterface.bean.User;
import com.gmallinterface.service.OrderDemoService;
import com.gmallinterface.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    @Reference
    UserService userService;
    @Reference
    OrderDemoService orderService;

    public User getOne(){
       return userService.getOneUser();
    }
    public Order getOrder(){
       return orderService.getOrder();
    }
}
