package com.orderserviceconsumer.controller;

import cn.wonhigh.retail.fas.cashier.api.service.StoreLockApi;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gmallinterface.bean.Order;
import com.gmallinterface.bean.User;
import com.orderserviceconsumer.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class OrderController {
    @Autowired
    private DemoService orderService;


    @Reference(version = "0.3.0",timeout = 50000000)
    private StoreLockApi storeLockApi;

    @GetMapping("/getUser")
    public User getUser() throws Exception{
        storeLockApi.isLockByStoreAndSaleDay("a",new Date());
        return orderService.getOne();
    }
    @GetMapping("/getOrder")
    public Order getOrder(){
        return orderService.getOrder();
    }
}
