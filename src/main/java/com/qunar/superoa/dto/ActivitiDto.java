package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/26.
 */
@Data
public class ActivitiDto {

    @ApiModelProperty("流程Id")
    private String deploymentId;

    @ApiModelProperty(value = "流程Name")
    private String name;

    @ApiModelProperty(value = "流程key")
    private String key;

    @ApiModelProperty(value = "流程版本")
    private String version;

    @ApiModelProperty(value = "流程部署时间")
    private String deployDateTime;

    @ApiModelProperty(value = "流程实例总数")
    private Long flowCount;

    @ApiModelProperty(value = "流程未完成实例数")
    private Long flowUnFinishedCount;

    @ApiModelProperty(value = "流程状态")
    private String status;

    @ApiModelProperty(value = "图片名称")
    private String imgName;

    @ApiModelProperty(value = "流程定义id")
    private String processDefinitionId;

}
