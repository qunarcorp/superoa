package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/12.
 */
@Data
public class ApproveCountDto {

    @ApiModelProperty(value = "待办条数")
    private int approveCount;

    @ApiModelProperty(value = "已办条数")
    private int approved;

    @ApiModelProperty(value = "外部系统待办条数")
    private int extSysApproveCount;
}
