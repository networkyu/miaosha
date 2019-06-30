package com.miaoshaproject.service.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/30
 */
@Data
public class PromoModel {
    private Integer id;
    // 活动名称
    private String promoName;
    //秒杀活动的开始时间
    private DateTime startDate;
    // 秒杀活动结束时间
    private DateTime endDate;
    // 秒杀活动使用商品
    private Integer itemId;
    //秒杀活动商品价格
    private BigDecimal promoItemPrice;
    // 秒杀活动状态 1：还未开始，2：进行中，3：已结束。
    private Integer status;
}
