package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/12.
 */
@Data
public class FlowModelNameDto {

    @ApiModelProperty(value = "流程表单模板名称(唯一值)")
    private String formName;

    @ApiModelProperty(value = "流程表单模板key(唯一值)")
    private String formKey;

    @ApiModelProperty(value = "流程表单所属系统")
    private String formMark;

    @ApiModelProperty(value = "员工部门Id")
    private String unitId;

    public FlowModelNameDto() {

    }

    public FlowModelNameDto(String formKey, String formName) {
        this.formKey = formKey;
        this.formName = formName;
    }

    public FlowModelNameDto(String formKey, String formName, String formMark, String unitId) {
        this.formKey = formKey;
        this.formName = formName;
        this.formMark = formMark;
        this.unitId = unitId;
    }
}
