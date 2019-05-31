package com.miaoshaproject.dao;
import com.miaoshaproject.dataobject.ItemStockDO;

public interface ItemStockDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);

    ItemStockDO selectByPrimaryKey(Integer id);
    // 通过itemId查询库存数量
    ItemStockDO selectByItemId(Integer itemId);
    int updateByPrimaryKeySelective(ItemStockDO record);

    int updateByPrimaryKey(ItemStockDO record);
}