package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer customer);

    List<String> getCustomerName(String name);

    int getTotalByCondition(Map<String, Object> map);

    List<Customer> getCustomerListByCondition(Map<String, Object> map);

    Customer detail(String id);
}
