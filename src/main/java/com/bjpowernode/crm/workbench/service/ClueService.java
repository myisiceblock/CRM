package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndClue(String id);

    boolean update(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String clueId, String[] activityIds);

    Clue convert(String clueId);

    boolean addConvert(String clueId, Tran tran, String createBy);
}
