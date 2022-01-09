package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran tran);

    int getTotalByCondition(Map<String, Object> map);

    List<Tran> getTranListByCondition(Map<String, Object> map);

    Tran detail(String id);

    int changeStage(Tran tran);

    int getStageTotal();

    List<Map<String, Object>> getCharts();

    List<Tran> detailTranByCustomerId(String customerId);

    int delete(String id);
}
