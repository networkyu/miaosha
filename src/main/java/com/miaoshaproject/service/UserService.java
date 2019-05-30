package com.miaoshaproject.service;


import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    //定义通过用户id获取用户对象的方法。
    UserModel getUserById(Integer id);

    // 定义用户注册
    void register(UserModel userModel) throws BussinessException;
    // 用户登录
    UserModel validateLogin(String telphone,String encrptPassword) throws BussinessException;
}
