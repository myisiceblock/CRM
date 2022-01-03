package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map =new HashMap<String, List<DicValue>>();
        //将字典类型列表去除
        List<DicType> dicTypeList = dicTypeDao.getTypeList();

        //将字典类型列表遍历
        for (DicType dicType :
                dicTypeList) {
            //取得每一种类型的字典类型编码
            String code = dicType.getCode();

            //根据每一个字典类型取得字典值列表
            List<DicValue> dicValueList = dicValueDao.geListByCode(code);
            map.put(code+"List",dicValueList);
        }
        return map;
    }
}
