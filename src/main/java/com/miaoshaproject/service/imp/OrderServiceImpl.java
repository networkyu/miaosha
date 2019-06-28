package com.miaoshaproject.service.imp;

import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.dataobject.OrderDO;
import com.miaoshaproject.dataobject.SequenceDO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.util.resources.LocaleData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/28
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;

    @Autowired
    OrderDOMapper orderDOMapper;

    @Autowired
    SequenceDOMapper sequenceDOMapper;
    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BussinessException {
        // 1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确。
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }
        if (amount <=0 || amount > 99){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        // 2.落单减库存。
        Boolean result = itemService.decreaseStock(itemId,amount);
        if (!result){
            throw  new BussinessException(EmBussinessError.STOCK_NOT_ENOUGH);
        }

        // 3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));
        // -- 生成交易流水号
        orderModel.setId(generateOrderNo());

        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);
        // 将产品的销量增加amount--最后做。
        itemService.increaseSales(itemId,amount);
        // 返回前端
        return orderModel;
    }
    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

//    public static void main(String[] args) {
//        LocalDateTime now = LocalDateTime.now();
//        String time = now.format(DateTimeFormatter.ISO_DATE);
//        System.out.println(time);
//    }

    /**
     * 订单号逻辑
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo(){
        // 订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        // 前8位为时间信息，年月日。
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
        //中间6位位自增序列。
        // 获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        if (sequenceStr.length()>6){
            // 如果自增序列到100000时开始值复位为0
            sequenceDO.setCurrentValue(0);
            sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        }
        for (int i = 0;i<6-sequenceStr.length();i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        // 最后两位为分库分表,暂时写死。
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
