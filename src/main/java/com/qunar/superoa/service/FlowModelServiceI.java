package com.qunar.superoa.service;

import com.qunar.superoa.dto.DeptDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.FlowModelNameDto;
import com.qunar.superoa.dto.FlowStatusDto;
import com.qunar.superoa.model.FlowModel;

import java.util.List;

/**
 * Created by xing.zhou on 2018/9/7.
 */
public interface FlowModelServiceI {

  /**
   * 添加模板
   * @param flowModelDto
   * @return
   * @throws Exception
   */
  FlowModelDto addFlowMode(FlowModelDto flowModelDto) throws Exception;

  /**
   * 修改模板
   * @param flowModelDto
   * @return
   * @throws Exception
   */
  FlowModelDto updateFlowMode(FlowModelDto flowModelDto) throws Exception;

  /**
   * 获取所有部门
   * @return
   */
  List<String> getFlowModelAllDept();

  /**
   * 根据条件获取流程模版
   *
   * @param dept 查询条件类
   * @return 表单formName，formKey
   */
  Object getFlowModels(DeptDto dept);


  List<FlowModelNameDto> getFlowModelsByDept(String dept);

  /**
   * 根据部门获取模板分页
   * @param dept
   * @return
   */
  Object getFlowModelsPageAble(DeptDto dept);

  /**
   * 获取我的热门模板
   * @return
   */
  List<FlowModelNameDto> getMyFlowModels();

  /**
   * 根据formKey获取最新版本模板
   * @param formKey
   * @return
   */
  FlowModelDto getFlowModelDtoByFormKey(String formKey);

  /**
   * 根据formKey获取最新版本模板
   * @param formKey
   * @return
   */
  FlowModel getFlowModelByFormKey(String formKey);

  /**
   * 根据版本号和formKey 获取模板Dto
   * @param formKey
   * @param version
   * @return
   */
  FlowModelDto getFlowModelDtoByFormKeyAndVersion(String formKey, String version);

  /**
   * 根据版本号和formKey 获取模板
   * @param formKey
   * @param version
   * @return
   */
  FlowModel getFlowModelByFormKeyAndVersion(String formKey, String version);

  /**
   * 根据表单名称获取最新版本模板
   * @param formName
   * @return
   */
  FlowModelDto getFlowModelByFormName(String formName);

  /**
   * 修改模板状态
   * @param flowStatusDto
   * @return
   */
  FlowModelDto updateFlowModelStatus(FlowStatusDto flowStatusDto);

  /**
   * 根据公司名称获取公司全部一级部门
   */
  List<String> getAllDeptByCompany();

}
