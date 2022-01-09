package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.CustomerService;

import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Map;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);

    public List<String> getCustomerName(String name) {
        List<String> sList = customerDao.getCustomerName(name);
        return sList;
    }

    public PaginationVO<Customer> pageList(Map<String, Object> map) {
        int total = customerDao.getTotalByCondition(map);
        List<Customer> customers = customerDao.getCustomerListByCondition(map);
        PaginationVO<Customer> customerPaginationVO = new PaginationVO<Customer>();
        customerPaginationVO.setTotal(total);
        customerPaginationVO.setDataList(customers);
        return customerPaginationVO;
    }

    public Customer detail(String id) {
        Customer customer = customerDao.detail(id);
        return customer;
    }

    public List<Tran> detailTranByCustomerId(String customerId) {
        List<Tran> tranList = tranDao.detailTranByCustomerId(customerId);
        return tranList;
    }
}


