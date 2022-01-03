package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public boolean save(Tran tran, String customerName) {
        boolean flag = true;
        Customer customer = customerDao.getCustomerByName(customerName);
        if(customer == null){
           customer = new Customer();
           customer.setId(UUIDUtil.getUUID());
           customer.setName(customerName);
           customer.setCreateBy(tran.getCreateBy());
           customer.setCreateTime(DateTimeUtil.getSysTime());
           customer.setContactSummary(tran.getContactSummary());
           customer.setOwner(tran.getOwner());
           customer.setNextContactTime(tran.getNextContactTime());
           int count1 = customerDao.save(customer);
           if(count1 != 1){
               flag = false;
           }
        }
        tran.setCustomerId(customer.getId());
        int count2 = tranDao.save(tran);
        if(count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setCreateBy(tran.getCreateBy());
        int count3 = tranHistoryDao.save(tranHistory);
        if(count3 != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Tran> pageList(Map<String, Object> map) {
        //取得total
        int total = tranDao.getTotalByCondition(map);
        //取得dataList
        List<Tran> dataList = tranDao.getTranListByCondition(map);
        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Tran> vo = new PaginationVO<Tran>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistoryList = tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistoryList;
    }

    public Map<String, Object> getCharts() {
        //取得total
        int total = tranDao.getStageTotal();
        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }

    public boolean changeStage(Tran tran) {
        boolean flag = true;
        //改变交易阶段
        int count1 = tranDao.changeStage(tran);
        if(count1 != 1){
            flag = false;
        }
        //交易阶段改变后，生成一条交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setTranId(tran.getId());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setStage(tran.getStage());
        int count2 = tranHistoryDao.save(tranHistory);
        if(count2 != 1){
            flag = false;
        }
        return flag;
    }
}
