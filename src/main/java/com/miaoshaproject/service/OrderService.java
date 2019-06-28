package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/28
 */
public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BussinessException;
}
