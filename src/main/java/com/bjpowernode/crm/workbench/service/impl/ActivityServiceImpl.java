package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    public boolean save(Activity activity) {

        boolean flag = true;
        int count = activityDao.save(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = true;

        //查询出需要删除备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注，返回收到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);

        if(count1 != count2){
            flag = false;
        }
        //删除市场活动
        int count3 = activityDao.delete(ids);
        if(count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {
        //取uList
        List<User> uList = userDao.getUserList();
        //取activity
        Activity activity = activityDao.getById(id);
        //将uList和activity打包到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userList",uList);
        map.put("activity",activity);
        //返回map就可以了
        return map;
    }

    public boolean update(Activity activity) {
        boolean flag = true;
        int count = activityDao.update(activity);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Activity detail(String id) {
        Activity activity = activityDao.detail(id);
        return activity;
    }

    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> activityRemarks = activityRemarkDao.getRemarkListByAid(activityId);
        return activityRemarks;
    }

    public boolean deleteRemark(String remarkId) {
        boolean flag = true;
        int result = activityRemarkDao.deleteRemark(remarkId);
        if(result != 1){
            flag = false;
        }
        return flag;
    }

    public boolean saveRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int result = activityRemarkDao.saveRemark(activityRemark);
        if(result != 1){
            flag = false;
        }
        return flag;
    }

    public ActivityRemark getRemarkById(String id) {
        ActivityRemark activityRemark = activityRemarkDao.getRemarkById(id);
        return activityRemark;
    }

    public boolean updateRemark(ActivityRemark activityRemark) {
        boolean flag = true;
        int result = activityRemarkDao.updateRemark(activityRemark);
        if(result != 1){
            flag = false;
        }
        return flag;
    }

    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);
        return activityList;
    }

    public List<Activity> getActivityByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> activityList = activityDao.getActivityByNameAndNotByClueId(map);
        return activityList;
    }

    public List<Activity> getActivtyList() {
        List<Activity> activityList = activityDao.getActivityList();
        return activityList;
    }

    public List<Activity> getActivtyByName(String activityName) {
        List<Activity> activityList = activityDao.getActivtyByName(activityName);
        return activityList;
    }
}
