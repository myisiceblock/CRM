package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String id);

    int deleteRemark(String remarkId);

    int saveRemark(ActivityRemark activityRemark);

    ActivityRemark getRemarkById(String id);

    int updateRemark(ActivityRemark activityRemark);
}
