package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ContactsService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ContactsServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;
import netscape.security.UserTarget;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");

        String servletPath = request.getServletPath();
        if("/workbench/customer/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/customer/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/customer/detailTranByCustomerId.do".equals(servletPath)){
            detailTranByCustomerId(request,response);
        }
    }

    private void detailTranByCustomerId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到跳转客户详细页获取交易列表");
        String customerId = request.getParameter("customerId");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<Tran> tranList = customerService.detailTranByCustomerId(customerId);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        for (Tran t :
                tranList) {
            String stage = t.getStage();
            String possibility = pMap.get(stage);
            t.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,tranList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转客户详细列表页");
        String id = request.getParameter("id");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        Customer customer = customerService.detail(id);
        request.setAttribute("customer",customer);
        request.getRequestDispatcher("/workbench/customer/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到获取客户列表");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("phone",phone);
        map.put("website",website);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        PaginationVO<Customer> customerPaginationVO =  customerService.pageList(map);
        PrintJson.printJsonObj(response,customerPaginationVO);
    }
}
