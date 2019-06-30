package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/28
 */
public interface OrderService {
    //使用1，通过前端url上传过来的秒杀活动id，然后下单接口内校验对应id是否属于对应商品且活动已经开始
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BussinessException;
}
