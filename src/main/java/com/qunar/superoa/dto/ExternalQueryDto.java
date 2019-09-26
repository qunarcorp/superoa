package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author zhouxing
 * @date 2019-02-28 11:25
 */
@Data
public class ExternalQueryDto {

  @ApiModelProperty(value = "流程表单模板key(唯一值)")
  private String formKey;

  @ApiModelProperty(value = "appCode")
  private String appCode;

  @ApiModelProperty(value = "版本号")
  private String version;

  @ApiModelProperty(value = "表单")
  private Map formDatas;

  @ApiModelProperty(value = "发起人qtalk")
  private String currentUserId;

  @ApiModelProperty(value = "发起人name")
  private String currentUserName;

}
