package com.qunar.superoa.service;

import com.qunar.superoa.dto.ExternalFlowModelDto;
import com.qunar.superoa.dto.ExternalQueryDto;
import com.qunar.superoa.model.FlowModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhouxing
 * @date 2019-02-27 20:51
 */
public interface FromExternalServiceI {

  /**
   * 查询flowModel
   *
   * @param formKey
   * @param appCode
   * @return
   */
  ExternalFlowModelDto getFlowModelByFormKey(String formKey, String appCode);


  /**
   * 发起流程
   *
   * @param formKey
   * @param appCode
   * @param formDatas
   * @return
   */
  String startFlow(HttpServletRequest request, ExternalQueryDto externalQueryDto);
}
