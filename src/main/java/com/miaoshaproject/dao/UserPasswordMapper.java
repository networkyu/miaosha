package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.UserPassword;

public interface UserPasswordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPassword record);

    int insertSelective(UserPassword record);

    UserPassword selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserPassword record);

    int updateByPrimaryKey(UserPassword record);
    //自定义的方法
    UserPassword selectByUserId(Integer user_id);

}