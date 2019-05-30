package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {
    @Autowired
    UserService userService ;
    @Autowired
    HttpServletRequest httpServletRequest;
    // 用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telphone") String telphone,
                                  @RequestParam(name="password") String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)){
            throw new BussinessException(EmBussinessError.USER_LOGIN_FAILURE);
        }
        // 用户登录服务，用来校验用户登录是否合法。
        UserModel userModel = userService.validateLogin(telphone,this.EncodeByMd5(password));
        // 将登录凭证加入到用户登录成功的session内。
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        return CommonReturnType.create(null);
    }
    // 用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone") String telphone,
                                     @RequestParam(name="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name="gender") Integer gender,
                                     @RequestParam(name="age") Integer age,
                                     @RequestParam(name="password") String password
                                     ) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }
    // md5混淆
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException,UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密字符串
        String newString = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newString;
    }
    // 通过userId获取用户信息
    @ResponseBody
    @RequestMapping(value = "/get")
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BussinessException {
        // 调用service服务获取对应id的用户对象并返回给前端。
        UserModel userModel = userService.getUserById(id);
        if (userModel == null){
            throw new BussinessException(EmBussinessError.USER_NOT_EXIST);
        }
        return CommonReturnType.create(convertFromModel(userModel));

    }
    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType getOpt(@RequestParam(name="telphone") String telphone) throws BussinessException{
        if (telphone==null||telphone.equals("")){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"请输入正确的手机号");
        }
        // 需要按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt = randomInt+10000;
        String otpCode = String.valueOf(randomInt);
        // 将otp验证码同对应用户的手机号关联，使用httpsession的方式绑定他的手机号与otpcode (企业级引用使用redis)
        httpServletRequest.getSession().setAttribute(telphone,otpCode);
        //将otp验证码通过短信通道发送给用户省略.
        System.out.println("telphone："+telphone+"    otpCode:"+otpCode);
        CommonReturnType commonReturnType = CommonReturnType.create(null);
        return commonReturnType;

    }
    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

}
