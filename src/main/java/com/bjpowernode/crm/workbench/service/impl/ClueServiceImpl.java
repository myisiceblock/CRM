package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao remarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);;
    //用户相关表
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    public boolean save(Clue clue) {
        boolean flag = true;
        int count = clueDao.save(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Clue> pageList(Map<String, Object> map) {
        //取得total
        int total = clueDao.getTotalByCondition(map);
        //取得dataList
        List<Clue> dataList = clueDao.getClueListByCondition(map);
        //创建一个vo对象，将total和dataList封装到vo中
        PaginationVO<Clue> vo = new PaginationVO<Clue>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = true;

        //查询出需要删除备注的数量
        int count1 = remarkDao.getCountByAids(ids);
        //删除备注，返回收到影响的条数（实际删除的数量）
        int count2 = remarkDao.deleteByAids(ids);

        if(count1 != count2){
            flag = false;
        }
        //删除市场活动
        int count3 = clueDao.delete(ids);
        if(count3 != ids.length){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getUserListAndClue(String id) {
        //取uList
        List<User> uList = userDao.getUserList();
        //取activity
        Clue clue = clueDao.getById(id);
        //将uList和activity打包到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userList",uList);
        map.put("clue",clue);
        //返回map就可以了
        return map;
    }

    public boolean update(Clue clue) {
        boolean flag = true;
        int count = clueDao.update(clue);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public Clue detail(String id) {
        Clue clue = clueDao.detail(id);
        return clue;
    }

    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    public boolean bund(String clueId, String[] activityIds) {
        boolean flag = true;
        for (String aid : activityIds){
            //取得每一个activityId和clueId做关联
            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtil.getUUID());
            clueActivityRelation.setActivityId(aid);
            clueActivityRelation.setClueId(clueId);
            int count = clueActivityRelationDao.bund(clueActivityRelation);
            if(count != 1){
                flag = false;
            }
        }

        return flag;
    }

    public Clue convert(String clueId) {
        Clue clue = clueDao.convert(clueId);
        return clue;
    }

    public boolean addConvert(String clueId, Tran tran, String createBy) {
        String createTime = DateTimeUtil.getSysTime();
        boolean flag = true;
        //1.通过线索id获取线索对象(线索对象当中封装了线索的信息)
        Clue clue = clueDao.getById(clueId);
        //2.通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司名的名称精确匹配，判断该客户是否存在）
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        //如果customer为null，说明以前没有这个客户，需要新建一个
        if (customer == null) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(clue.getCompany());
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            //添加客户
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }
        //3.通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setAppellation(clue.getAppellation());
        contacts.setMphone(clue.getMphone());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setEmail(clue.getEmail());
        contacts.setJob(clue.getJob());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAddress(clue.getAddress());
        contacts.setFullname(clue.getFullname());
        //添加联系人
        int count2 =contactsDao.save(contacts);
        if(count2 != 1){
            flag = false;
        }
        //4.线索备注转换到客户备注以及联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = remarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark :
                clueRemarkList) {
            //取出备注信息（主要转换到客户备注和联系人备注的就是这个备注信息）
            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){
                flag = false;
            }
            //创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
        }

        //5."线索和市场活动“的关系转换到”联系人和市场活动“的关系
        //查询出与该条线索关联的市场活动，查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelations = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation :
                clueActivityRelations) {
            //从每一条遍历出来的记录中取出关联的市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //创建联系人与市场活动的关联关系对象，让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }
        //6.如果有创建交易的要求，创建一条交易
        if(tran != null){
            //接下来可以通过第一步生成的clue对象，取出一些信息，继续完善对tran对象的封装
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setContactsId(contacts.getId());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            //添加交易
            int count6 = tranDao.save(tran);
            if(count6 != 1){
                flag = false;
            }
            //7.如果创建了交易，则创建一条该交一下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());
            //添加交易历史
            int count7 = tranHistoryDao.save(tranHistory);
            if(count7 != 1){
                flag = false;
            }
        }
        //8.删除线索备注
        for(ClueRemark clueRemark:clueRemarkList){
            int count8 = remarkDao.delete(clueRemark);
            if(count8 != 1){
                flag = false;
            }
        }
        //9.删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation:
             clueActivityRelations) {
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9 != 1){
                flag = false;
            }
        }
        //10.删除线索
        int count10 = clueDao.deleteById(clueId);
        if(count10 != 1){
            flag = false;
        }
        return flag;
    }

}
