package com.provider3.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmallinterface.bean.User;
import com.gmallinterface.service.UserService;
import org.springframework.beans.factory.annotation.Value;


@Service
public class UserServiceImpl implements UserService {
    @Value("${server.port}")
    private String port;

    @Override
    public User getOneUser() {
        return new User(port+"daiming","sz",27,"man");
    }

    @Override
    public String getUserName() {
        return "jtt";
    }
}
