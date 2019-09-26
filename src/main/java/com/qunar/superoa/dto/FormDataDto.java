package com.qunar.superoa.dto;

import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * Created by xing.zhou on 2018/8/30.
 */
@Data
public class FormDataDto {

    @ApiModelProperty(value = "流程Id",  required = false)
    private String procInstId;

    @ApiModelProperty(value = "流程key",  required = true)
    private String flowKey;

    @ApiModelProperty(value = "流程名称",  required = false)
    private String flowName;

    @ApiModelProperty(value = "表单数据",  required = true)
    private Map formDatas;

    public void setFormDatas(Object formDatas) {
        this.formDatas = CommonUtil.o2m(formDatas);
    }
}
