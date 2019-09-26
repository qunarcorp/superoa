package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 1:49 PM 2018/10/25
 * @Modify by:
 */

@Data
@ApiModel("opsapp 审批 返回结果信息")
public class OpsappApproveDto<T> {

  @ApiModelProperty(value = "errcode", name = "errcode")
  private String e;

  @ApiModelProperty(value = "message", name = "message")
  private String m;

 /** 具体的内容 */
  @ApiModelProperty(value = "数据对象", name = "数据对象")
  private T d;
}
