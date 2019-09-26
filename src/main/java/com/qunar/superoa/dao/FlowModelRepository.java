package com.qunar.superoa.dao;

import com.qunar.superoa.model.FlowModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xing.zhou on 2018/9/7.
 */
public interface FlowModelRepository extends JpaRepository<FlowModel, String>,JpaSpecificationExecutor<FlowModel> {


    /**
     * 查询出表单中的所有部门
     * @return 部门名称列表
     */
    @Query(value = "select distinct formDept from FlowModel")
    List<String> findAllDept();

    /**
     * 根据部门查找所有表单
     * @param dept 部门名称
     * @return 表单列表
     */
    List<FlowModel> findByFormDept(String dept);

    /**
     * 根据表单FormKey查询出最新版本号的表单
     * @param formKey 表单FormKey
     * @return 最新表单
     */
    FlowModel findFirstByFormKeyOrderByFormVersionDesc(String formKey);

    /**
     * 根据表单FormKey和版本号查询表单
     * @param formKey 表单FormKey
     * @param formVersion 表单版本号
     * @return 对应表单
     */
    FlowModel findByFormKeyAndFormVersion(String formKey, Integer formVersion);

    /**
     * 根据表单FormName查询出最新版本号的表单
     * @param formName 表单FormName
     * @return 最新表单
     */
    FlowModel findFirstByFormNameOrderByFormVersionDesc(String formName);

    /**
     * 根据表单FormKey和表单状态查询表单
     * @param formKey 表单FormKey
     * @param flowStatus 表单状态
     */
    FlowModel findFirstByFormKeyAndFlowStatusOrderByFormVersionDesc(String formKey, int flowStatus);

    /**
     * 根据表单状态查询出所有对应表单
     * @param status 表单状态
     * @param formKey 表单key
     * @return 表单列表
     */
    List<FlowModel> findAllByFlowStatusAndFormKey(Integer status, String formKey);

    /**
     * 根据表单状态查询出所有对应表单
     * @param status 表单状态
     * @return 表单列表
     */
    List<FlowModel> findAllByFlowStatus(Integer status);
}
