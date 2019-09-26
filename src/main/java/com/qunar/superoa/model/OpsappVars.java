package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction: opsapp审批具体参数
 * @Date: Created in 2:01 PM 2018/10/25
 * @Modify by:
 */

@Data
@ApiModel("opsapp审批具体参数")
public class OpsappVars {


  @ApiModelProperty(value = "id", name = "id", required = true)
  private String taskIds;

  @ApiModelProperty(value = "审批操作", name = "审批操作", required = true)
  private String approve;

  @ApiModelProperty(value = "原因", name = "原因", required = false)
  private String reason;

  @ApiModelProperty(value = "查询条数", name = "查询条数", required = false)
  private String length;

  @ApiModelProperty(value = "processKeys = super-oa")
  private String processKeys;

  @ApiModelProperty("开始条目")
  private String start;

  @ApiModelProperty("user")
  private String user;

  @ApiModelProperty("keywords")
  private String keywords;

  @ApiModelProperty("表单数据")
  private String formDatas;
}
