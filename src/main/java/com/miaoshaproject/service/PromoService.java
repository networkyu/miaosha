package com.miaoshaproject.service;

import com.miaoshaproject.service.model.PromoModel;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/30
 */
public interface PromoService {
    // 根据itemId获取即将进行，或者正则进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
