package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");

        String servletPath = request.getServletPath();
        if("/workbench/activity/getUserList.do".equals(servletPath)){
            getUserList(request,response);
        }else if("/workbench/activity/save.do".equals(servletPath)){
            save(request,response);
        }else if("/workbench/activity/pageList.do".equals(servletPath)){
            pageList(request,response);
        }else if("/workbench/activity/delete.do".equals(servletPath)){
            delete(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(servletPath)){
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/update.do".equals(servletPath)){
            update(request,response);
        }else if("/workbench/activity/detail.do".equals(servletPath)){
            detail(request,response);
        }else if("/workbench/activity/getRemarkListByAid.do".equals(servletPath)){
            getRemarkListByAid(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(servletPath)){
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(servletPath)){
            saveRemark(request,response);
        }else if("/workbench/activity/getRemarkById.do".equals(servletPath)){
            getRemarkById(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(servletPath)){
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到修改备注操作");

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editFlag = "1";
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setEditFlag(editFlag);
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setEditTime(editTime);
        activityRemark.setEditBy(editBy);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.updateRemark(activityRemark);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("跳转到修改备注页");

        String id = request.getParameter("id");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        ActivityRemark activityRemark = activityService.getRemarkById(id);
        PrintJson.printJsonObj(response,activityRemark);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到添加备注操作");

        String id = UUIDUtil.getUUID();
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String editFlag = "0";

        ActivityRemark activityRemark = new ActivityRemark();
        activityRemark.setCreateTime(createTime);
        activityRemark.setId(id);
        activityRemark.setNoteContent(noteContent);
        activityRemark.setActivityId(activityId);
        activityRemark.setCreateBy(createBy);
        activityRemark.setEditFlag(editFlag);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.saveRemark(activityRemark);
        PrintJson.printJsonFlag(response,flag);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到删除备注操作");

        String remarkId = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.deleteRemark(remarkId);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据市场活动id，取得备注列表");

        String activityId = request.getParameter("activityId");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarks = activityService.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,activityRemarks);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到详细信息页的操作");

        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity =activityService.detail(id);
        request.setAttribute("activity",activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到修改市场活动操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //修改时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人，当前登录用户
        String editBy = ((User) request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();
        activity.setId(id);
        activity.setCost(cost);
        activity.setEditBy(editBy);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEndDate(endDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setStartDate(startDate);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag =  activityService.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");

        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        /*
            总结：
                controller调用service的方法，返回值应该是什么
                得想想前端要什么，就要从service层去什么

            前端需要得，管业务层去要
            uList
            activity

            以上两项信息 ，复用率不高，我们选择使用map打包这两项信息即可
            map
         */
        Map<String,Object> map = activityService.getUserListAndActivity(id);

        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到删除市场活动操作");
        String ids[] = request.getParameterValues("id");

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = activityService.delete(ids);

        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询+分页查询）");
        String name =request.getParameter("name");
        String owner =request.getParameter("owner");
        String startDate =request.getParameter("startDate");
        String endDate =request.getParameter("endDate");
        String pageNoStr =request.getParameter("pageNo");
        //每页展现的记录数
        String pageSizeStr =request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
            前端要：市场活动信息列表
                    查询的总条数
         */
        PaginationVO<Activity> activities = activityService.pageList(map);

        PrintJson.printJsonObj(response,activities);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动添加操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();
        activity.setId(id);
        activity.setCost(cost);
        activity.setCreateBy(createBy);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setEndDate(endDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setStartDate(startDate);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag =  activityService.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }


    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
