package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");

        String servletPath = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(servletPath)){
            getUserList(request,response);
        }else if("/workbench/clue/save.do".equals(servletPath)){
            save(request,response);
        }else if("/workbench/clue/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/clue/delete.do".equals(servletPath)){
            delete(request,response);
        }else if("/workbench/clue/getUserListAndClue.do".equals(servletPath)){
            getUserListAndClue(request,response);
        }else if("/workbench/clue/update.do".equals(servletPath)){
            update(request,response);
        }else if("/workbench/clue/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(servletPath)){
            getActivityListByClueId(request,response);
        }else if("/workbench/clue/unbund.do".equals(servletPath)){
            unbund(request,response);
        }else if("/workbench/clue/getActivityByNameAndNotByClueId.do".equals(servletPath)){
            getActivityByNameAndNotByClueId(request,response);
        }else if("/workbench/clue/bund.do".equals(servletPath)){
            bund(request,response);
        }else if("/workbench/clue/convert.do".equals(servletPath)){
            convert(request,response);
        }else if("/workbench/clue/getActivityList.do".equals(servletPath)){
            getActivityList(request,response);
        }else if("/workbench/clue/getActivityByName.do".equals(servletPath)){
            getActivityByName(request,response);
        }else if("/workbench/clue/addConvert.do".equals(servletPath)){
            addConvert(request,response);
        }
    }

    private void addConvert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行线索转换模块");
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        Tran tran = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        //如果需要创建交易
        if("true".equals(flag)){
            tran = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            tran.setId(id);
            tran.setMoney(money);
            tran.setName(name);
            tran.setExpectedDate(expectedDate);
            tran.setStage(stage);
            tran.setActivityId(activityId);
            tran.setCreateTime(createTime);
            tran.setCreateBy(createBy);
        }
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag1 = clueService.addConvert(clueId,tran,createBy);

        if(flag1){
            response.sendRedirect(request.getContextPath()+ "/workbench/clue/index.jsp");
        }
    }

    private void getActivityByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表（根据名称模糊查）");
        String activityId = request.getParameter("activityName");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivtyByName(activityId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void getActivityList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取所有市场活动列表");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivtyList();
        PrintJson.printJsonObj(response,activityList);
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到转换线索模块");

        String clueId = request.getParameter("clueId");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.convert(clueId);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/convert.jsp").forward(request,response);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行关联市场活动的操作");
        String clueId = request.getParameter("clueId");
        String[] activityIds = request.getParameterValues("activityId");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.bund(clueId,activityIds);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动列表(根据名称模糊查+排除掉已经关联指定线索的列表)");

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<String, String>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到解除关联操作");

        String id = request.getParameter("id");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索ID查询关联的市场活动列表");

        String clueId = request.getParameter("clueId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = activityService.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到详细信息页的操作");

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = clueService.detail(id);
        request.setAttribute("clue",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到修改线索模块操作");


        Clue clue = new Clue();
        clue.setId(request.getParameter("id"));
        clue.setFullname(request.getParameter("fullname"));
        clue.setAppellation(request.getParameter("appellation"));
        clue.setOwner(request.getParameter("owner"));
        clue.setCompany(request.getParameter("company"));
        clue.setJob(request.getParameter("job"));
        clue.setEmail(request.getParameter("email"));
        clue.setPhone(request.getParameter("phone"));
        clue.setWebsite(request.getParameter("website"));
        clue.setMphone(request.getParameter("mphone"));
        clue.setState(request.getParameter("state"));
        clue.setSource(request.getParameter("source"));
        clue.setEditTime(DateTimeUtil.getSysTime());
        clue.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        clue.setDescription(request.getParameter("description"));
        clue.setContactSummary( request.getParameter("contactSummary"));
        clue.setNextContactTime(request.getParameter("nextContactTime"));
        clue.setAddress(request.getParameter("address"));

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.update(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndClue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("跳转到修改线索操作");

        String id = request.getParameter("id");
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Map<String,Object> map = clueService.getUserListAndClue(id);

        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到删除线索操作");
        String ids[] = request.getParameterValues("id");

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.delete(ids);

        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到获取全部线索操作");

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String owner = request.getParameter("owner");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String pageNoStr =request.getParameter("pageNo");
        //每页展现的记录数
        String pageSizeStr =request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        /*
            前端要：市场活动信息列表
                    查询的总条数
         */
        PaginationVO<Clue> cluePaginationVO = clueService.pageList(map);

        PrintJson.printJsonObj(response,cluePaginationVO);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到添加线索模块操作");


        Clue clue = new Clue();
        clue.setId(UUIDUtil.getUUID());
        clue.setFullname(request.getParameter("fullname"));
        clue.setAppellation(request.getParameter("appellation"));
        clue.setOwner(request.getParameter("owner"));
        clue.setCompany(request.getParameter("company"));
        clue.setJob(request.getParameter("job"));
        clue.setEmail(request.getParameter("email"));
        clue.setPhone(request.getParameter("phone"));
        clue.setWebsite(request.getParameter("website"));
        clue.setMphone(request.getParameter("mphone"));
        clue.setState(request.getParameter("state"));
        clue.setSource(request.getParameter("source"));
        clue.setCreateTime(DateTimeUtil.getSysTime());
        clue.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        clue.setDescription(request.getParameter("description"));
        clue.setContactSummary( request.getParameter("contactSummary"));
        clue.setNextContactTime(request.getParameter("nextContactTime"));
        clue.setAddress(request.getParameter("address"));
        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = clueService.save(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到取得用户信息列表");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = userService.getUserList();
        PrintJson.printJsonObj(response,users);
    }

}
