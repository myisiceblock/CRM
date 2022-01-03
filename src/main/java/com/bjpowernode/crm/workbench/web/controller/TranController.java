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
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.*;
import com.bjpowernode.crm.workbench.service.impl.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");

        String servletPath = request.getServletPath();
        if("/workbench/transaction/add.do".equals(servletPath)){
            add(request,response);
        }else if("/workbench/transaction/getContactsList.do".equals(servletPath)){
            getContactsList(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(servletPath)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/save.do".equals(servletPath)){
            save(request,response);
        }else if("/workbench/transaction/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/transaction/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/transaction/getHistoryListByTranId.do".equals(servletPath)){
            getHistoryListByTranId(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(servletPath)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(servletPath)){
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取交易阶段数量统计图标的数据");

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map =tranService.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行改变的操作");
        String tranId = request.getParameter("tranId");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran tran = new Tran();
        tran.setId(tranId);
        tran.setStage(stage);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setEditTime(editTime);
        tran.setEditBy(editBy);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.changeStage(tran);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        tran.setPossibility(pMap.get(stage));

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("tran",tran);

        PrintJson.printJsonObj(response,map);
    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取交易历史列表（根据交易ID）");

        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistoryList = tranService.getHistoryListByTranId(tranId);
        Map<String,Object> pMap = (Map<String, Object>) this.getServletContext().getAttribute("pMap");
        //将交易历史列表遍历
        for (TranHistory th : tranHistoryList){
            //根据每条交易历史，取出每一个阶段
            String stage = th.getStage();
            String possibility = (String) pMap.get(stage);
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,tranHistoryList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易跳转详细信息页");
        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran= tranService.detail(id);
        //处理可能性
        String stage = tran.getStage();
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);
        request.setAttribute("tran",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到交易列表");

        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsName");
        String pageNoStr = request.getParameter("pageNo");
        //每页展现的记录数
        String pageSizeStr =request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("owner",owner);
        map.put("name",name);
        map.put("customerName",customerName);
        map.put("stage",stage);
        map.put("type",type);
        map.put("source",source);
        map.put("contactsName",contactsName);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        PaginationVO<Tran> tranList = tranService.pageList(map);
        PrintJson.printJsonObj(response,tranList);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行添加交易的操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        Tran tran = new Tran();;
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(tran,customerName);
        if(flag){
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表（按照客户名称进行模糊查询）");

        String name = request.getParameter("name");

        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> stringList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,stringList);
    }

    private void getContactsList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查找联系人模态窗口");
        ContactsService contactsService = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        List<Contacts> contactsList = contactsService.getContactsList();
        PrintJson.printJsonObj(response,contactsList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("uList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
