package com.miaoshaproject.controller;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yulianpeng
 * @email network_u@163.com
 * Date 2019/6/28
 */
@Controller("/order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController {
    // 封装下单请求
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @RequestMapping(value = "/createorder",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount) throws BussinessException {
        // 获取用户的登录信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()){
            throw new BussinessException(EmBussinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        orderService.createOrder(userModel.getId(),itemId,amount);
        return CommonReturnType.create(null);
    }
}
