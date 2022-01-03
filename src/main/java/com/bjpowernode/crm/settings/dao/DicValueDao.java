package com.bjpowernode.crm.settings.dao;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> geListByCode(String code);
}
