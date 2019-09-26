package com.qunar.superoa.service;

import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.model.FormData;

/**
 * Created by xing.zhou on 2018/8/30.
 */
public interface FormDataServiceI {

  /**
   * 查询FromDateDto
   */
  FormDataDto getFromDataByProcInstId(String procInstId);

  /**
   * 查询FromDate
   */
  FormData findByProcInstId(String procInstId);

  /**
   * 新建保存FromData
   */
  FormData addFromData(FormDataDto formDataDto, FlowModel flowModel, boolean update);

  FormData updateFromData(String procInstId, String formDatas);
}
