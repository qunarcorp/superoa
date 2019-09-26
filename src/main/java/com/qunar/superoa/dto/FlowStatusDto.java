package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction: 更改表单状态中间类
 * @Date:Created in 上午11:07 2018/9/28
 * @Modify by:
 */
@Data
public class FlowStatusDto {

  @ApiModelProperty(value = "流程模版id(唯一值)", required = true)
  private String id ;

  @ApiModelProperty(value = "流程表单要被更改为的状态", required = false)
  private Integer flowStatus ;

}
