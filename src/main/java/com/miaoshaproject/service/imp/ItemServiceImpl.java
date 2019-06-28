package com.miaoshaproject.service.imp;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.ItemStockDO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Override
    // 事务
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BussinessException{
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //转化itemModel->dataObject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }
    // 库存减操作
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BussinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId,amount);
        if (affectedRow > 0){
           // 更新库存成功。
            return true;
        }
        return false;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null){
            return null;
        }
        // 操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        // 将dataobject->model
        ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
        return itemModel;
    }
    // 通过模型生成数据模型
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        return itemDO;

    }
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null ;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BussinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }
}
