package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction: opsapp 审批传来的参数
 * @Date: Created in 1:58 PM 2018/10/25
 * @Modify by:
 */

@Data
@ApiModel("opsapp 审批传来的参数")
public class OpsappAproveParams {

  @ApiModelProperty("系统名称")
  private String system;

  @ApiModelProperty("qtalk账号")
  private String rtx_id;

  @ApiModelProperty("opsapp审批具体参数")
  private OpsappVars vars;


}
