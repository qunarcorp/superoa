package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 9:12 PM 2019/1/15
 * @Modify by:
 */
@Data
public class FlowUserDto {
  @ApiModelProperty( value = "流程flowOrderID" )
  private String flowId;

  @ApiModelProperty( value = "被替换人userId" )
  private String oldUserId;

  @ApiModelProperty( value = "替换人userId,用英文逗号分隔" )
  private String newUserIds;

  @ApiModelProperty( value = "处理备注" )
  private String memo;

  @ApiModelProperty( value = "所做操作：替换 - updateApprover、添加-addApprover、删除-delApprover" )
  private String operationApprover;
}
