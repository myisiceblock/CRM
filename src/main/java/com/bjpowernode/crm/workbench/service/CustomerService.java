package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<String> getCustomerName(String name);

    PaginationVO<Customer> pageList(Map<String, Object> map);

    Customer detail(String id);

    List<Tran> detailTranByCustomerId(String customerId);
}
