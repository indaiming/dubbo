package com.gmallinterface.bean;


import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable{

    private Integer id;
    /**
     * 缴款单ID
     */
    private Integer orderId;
    /**
     * 商品编号
     */
    private String goodsNo;
    /**
     * 商品名称
     */
    private String goodsName;
}
