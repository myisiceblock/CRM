package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MyBatisUtils;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

import javax.jws.soap.SOAPBinding;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public User login(String loginAct, String loginPwd,String ip) throws LoginException {
        User user = userDao.login(loginAct, loginPwd);

        if(user == null){
            throw new LoginException("账号密码错误");
        }

        //如果程序能够成功的执行到该行，说明账号密码正确
        //验证失效时间
        String expireTime = user.getExpireTime();
        String currenTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currenTime) < 0){
            throw new LoginException("账号已失效");
        }

        //判断锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)) {
            throw new LoginException("账号已锁定，请联系管理员");
        }

        //判断ip地址
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }
        return user;
    }

    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }
}
