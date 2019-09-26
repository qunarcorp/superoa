package com.qunar.superoa.dto;

import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by xing.zhou on 2018/9/12.
 * 流程数据详情  传递前端数据
 */
@Data
public class FlowDataDto {

    @ApiModelProperty(value = "流程Id")
    private String procInstId;

    @ApiModelProperty(value = "流程key")
    private String flowKey;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "申请人Id")
    private String applyUserId;

    @ApiModelProperty(value = "申请人Name")
    private String applyUserName;

    @ApiModelProperty(value = "申请人部门")
    private String applyUserDept;

    @ApiModelProperty(value = "申请人全部部门")
    private String applyUserFullDept;

    @ApiModelProperty(value = "申请时间")
    private String applyTime;

    @ApiModelProperty(value = "节点审批人")
    private String approveUsers;

    @ApiModelProperty(value = "节点审批人头像")
    private String approveUsersAvatar;

    @ApiModelProperty(value = "是否可撤销及回退")
    private String canRevokeOrBack;

    @ApiModelProperty(value = "节点审批名称")
    private String approveNodeName;

    @ApiModelProperty( value = "表单特殊字段集合（hide:隐藏;edit:可编辑）")
    private List<Map<String, String>> editNodeName;

    @ApiModelProperty(value = "表单模板")
    private Map formModelJson;

    @ApiModelProperty(value = "表单数据")
    private Map formDatas;

    @ApiModelProperty(value = "审批日志")
    private List<ApproveLogDto> approveLogs;

    public void setFormModelJson(Object formModelJson) {
        this.formModelJson = CommonUtil.o2m(formModelJson);
    }

    public void setFormDatas(Object formDatas) {
        this.formDatas = CommonUtil.o2m(formDatas);
    }
}
