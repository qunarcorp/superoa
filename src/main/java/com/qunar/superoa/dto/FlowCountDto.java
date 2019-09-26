package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/11.
 */
@Data
public class FlowCountDto {
    @ApiModelProperty(value = "本人草稿流程数")
    private int draftCount;

    @ApiModelProperty(value = "本人审批中流程数")
    private int approvalCount;

    @ApiModelProperty(value = "本人已完成流程数")
    private int offCount;

    @ApiModelProperty(value = "本人已撤销流程数")
    private int revokeCount;

    @ApiModelProperty(value = "本人oa待审批流程数")
    private int oaUnApproveCount;

   @ApiModelProperty(value = "本人外部系统待审批流程数")
   private int extSysUnApproveCount;
}
