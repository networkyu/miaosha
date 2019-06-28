package com.miaoshaproject.dao;

import com.miaoshaproject.dataobject.ItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Integer id);
    // 商品查询列表
    List<ItemDO> listItem();

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);

    //增加销量
    int increaseSales(@Param("id")Integer id,@Param("amount")Integer amount);
}