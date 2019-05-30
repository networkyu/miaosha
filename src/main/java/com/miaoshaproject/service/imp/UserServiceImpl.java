package com.miaoshaproject.service.imp;

import com.miaoshaproject.dao.UserInfoMapper;
import com.miaoshaproject.dao.UserPasswordMapper;
import com.miaoshaproject.dataobject.UserInfo;
import com.miaoshaproject.dataobject.UserPassword;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator ;
    // 用户登录

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BussinessException{
//        通过用户手机号获取用户信息
        UserInfo userInfo = userInfoMapper.selectByTelphone(telphone);
        if (userInfo == null){
            throw new BussinessException(EmBussinessError.USER_LOGIN_FAILURE);
        }
        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = convertFromDataObject(userInfo,userPassword);
//        比对用户信息内密文是否和传输进来的密文匹配
        if (!com.alibaba.druid.util.StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BussinessException(EmBussinessError.USER_LOGIN_FAILURE);
        }
        return userModel;
    }

    @Override
    public UserModel getUserById(Integer id) {
        // 通过userInfoMapper获取用户的dataobject
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        UserPassword userPassword = userPasswordMapper.selectByUserId(id);
        return convertFromDataObject(userInfo,userPassword);
    }

    private UserModel convertFromDataObject(UserInfo userInfo, UserPassword userPassword){
        if (userInfo == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo,userModel);
        if (userPassword != null){
            userModel.setEncrptPassword(userPassword.getEncrptPassword());
        }
        return userModel;
    }

    @Override
    // 事务注解
    @Transactional
    public void register(UserModel userModel) throws BussinessException {
        if (userModel == null){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if (StringUtils.isEmpty(userModel.getName()) || userModel.getGender()==null|| userModel.getAge()==null || StringUtils.isEmpty(userModel.getTelphone())){
//            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        UserInfo userInfo = convertFromModel(userModel);
        try {
            userInfoMapper.insertSelective(userInfo);
        }catch (DuplicateKeyException e){
            throw  new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"手机号已经注册");
        }
        userModel.setId(userInfo.getId());
        UserPassword userPassword = convertPasswordFromModel(userModel);
        try {
            userPasswordMapper.insertSelective(userPassword);
        } catch (Exception e){
            throw  new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"密码问题");
        }
        return;
    }

    private UserInfo convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel,userInfo);
        return userInfo;
    }

    private UserPassword convertPasswordFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserPassword userPassword = new UserPassword();
        userPassword.setEncrptPassword(userModel.getEncrptPassword());
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }

}
