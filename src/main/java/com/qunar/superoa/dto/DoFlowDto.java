package com.qunar.superoa.dto;

import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/13_下午8:25
 * @Despriction:
 */

@Data
public class DoFlowDto {
    @ApiModelProperty(value = "流程实例ID", required = true)
    private String flowId;

    @ApiModelProperty(value = "审批意见")
    private String memo;

    @ApiModelProperty(value = "转交人userId - 即qtalk")
    private String forwardUserId;

    @ApiModelProperty(value = "表单数据")
    private Map formDatas;
}
