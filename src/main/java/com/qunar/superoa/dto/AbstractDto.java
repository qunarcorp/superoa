package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/7.
 */
@Data
public class AbstractDto {

    @ApiModelProperty(value = "字段名称", name = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段值key", name = "字段值key")
    private String valueKey;
}
