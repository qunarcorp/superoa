package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction: 集成系统待审批流程结果传送转换
 * @Date:Created in 6:02 PM 2019/4/26
 * @Modify by:
 */
@Data
public class ExtSysResultDto {

  @ApiModelProperty(value = "单条审批流程更新状态")
  private boolean isOk;

  @ApiModelProperty(value = "更新信息")
  private String msg = "";

  @ApiModelProperty(value = "操作方式")
  private String operation = "";

  @ApiModelProperty(value = "更新失败的审批流程信息")
  private String errorFlow = "";

}
