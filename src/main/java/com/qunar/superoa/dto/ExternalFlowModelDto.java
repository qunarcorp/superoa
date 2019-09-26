package com.qunar.superoa.dto;

import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxing
 * @date 2019-02-27 21:38
 */
@Data
public class ExternalFlowModelDto {

  @ApiModelProperty(value = "流程表单模板key(唯一值)", required = true)
  private String key;

  @ApiModelProperty(value = "流程表单模板名称(唯一值)", required = true)
  private String name;

  @ApiModelProperty(value = "字段组集合")
  private List groups;

  @ApiModelProperty(value = "版本号", required = true)
  private Integer version;

  public ExternalFlowModelDto(){

  }

  public ExternalFlowModelDto(FlowModel flowModel){
    key = flowModel.getFormKey();
    name = flowModel.getFormName();
    version = flowModel.getFormVersion();
    groups = (List) CommonUtil.s2m(flowModel.getFormModels()).get("groups");
  }

}
