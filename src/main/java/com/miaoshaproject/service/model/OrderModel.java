package com.miaoshaproject.service.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/28
 */
@Data
@ToString
public class OrderModel {
    // 订单号
    private String id;
    //用户id
    private  Integer userId;
    //商品id
    private  Integer itemId;
    // 购买数量
    private Integer amount;

    // 购买金额
    private BigDecimal orderPrice;

    // 购买商品的单价-当时购买的价格
    private BigDecimal itemPrice;

}
