package com.qunar.superoa.service;

import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.QueryWorkGroupDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.model.WorkGroup;
import org.springframework.data.domain.Page;

/**
 * created by chengyan.liang on 2018/9/25
 */
public interface GroupServiceI {

    /**
     * 添加一个工作组
     * @param workGroup 工作组
     * @return
     */
    WorkGroup saveWorkGroup(WorkGroup workGroup);

    /**
     * 根据id查询工作组
     * @param id 工作组id
     * @return
     */
    WorkGroup findWorkGroupById(Integer id);

    /**
     * 根据id删除工作组
     * @param id
     * @return
     */
    void deleteWorkGroupById(Integer id);

    /**
     * 更新WorkGroup
     * @param workGroup 工作组
     * @return
     */
    WorkGroup updateWorkGroup(WorkGroup workGroup);

    /**
     * 查询group列表
     * @param queryWorkGroupDto 工作组中间类
     * @return
     */
    PageResult<WorkGroup> findWorkGroupByLike(QueryWorkGroupDto queryWorkGroupDto);
}
