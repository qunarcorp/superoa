package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/12.
 */
@Data
public class ApproveLogDto {

    @ApiModelProperty(value = "审批人")
    private String approveUserId;

    @ApiModelProperty(value = "审批人头像")
    private String approveUserAvatar;

    @ApiModelProperty(value = "当前节点")
    private String nodeName;

    @ApiModelProperty(value = "下一节点")
    private String nextNodeName;

    @ApiModelProperty(value = "审批意见")
    private String memo;

    @ApiModelProperty(value = "审批时间")
    private String approveTime;
}
