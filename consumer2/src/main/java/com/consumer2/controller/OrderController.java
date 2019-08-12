package com.consumer2.controller;

import cn.wonhigh.retail.fas.cashier.api.service.StoreLockApi;
import com.alibaba.dubbo.config.annotation.Reference;
import com.consumer2.service.DemoService;
import com.gmallinterface.bean.Order;
import com.gmallinterface.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class OrderController {
//    @Autowired
//    private DemoService orderService;

    @Reference(version = "0.3.0",timeout = 50000000,retries = 3,check = false)
    private StoreLockApi storeLockApi;

//    @GetMapping("/getUser")
//    public User getUser(){
//        return orderService.getOne();
//    }
//    @GetMapping("/getOrder")
//    public Order getOrder(){
//        return orderService.getOrder();
//    }


    @GetMapping("/getUser11")
    public void getUser1() throws Exception{
        storeLockApi.isLockByStoreAndSaleDay("a",new Date());
//        return orderService.getOne();
    }
}
