package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/18.
 */
@Data
public class FormKeyDto {

    @ApiModelProperty("流程模板key")
    private String formKey;

    @ApiModelProperty("流程模板版本号")
    private String formVersion;
}
