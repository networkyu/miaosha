package com.miaoshaproject.dao;
import com.miaoshaproject.dataobject.ItemStockDO;
import org.apache.ibatis.annotations.Param;

public interface ItemStockDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);

    ItemStockDO selectByPrimaryKey(Integer id);
    // 通过itemId查询库存数量
    ItemStockDO selectByItemId(Integer itemId);
    int updateByPrimaryKeySelective(ItemStockDO record);

    int updateByPrimaryKey(ItemStockDO record);
    int decreaseStock(@Param("itemId")Integer itemId, @Param("amount")Integer amount);
}