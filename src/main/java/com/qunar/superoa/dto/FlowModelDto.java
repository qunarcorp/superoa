package com.qunar.superoa.dto;

import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Created by xing.zhou on 2018/9/7.
 */
@Data
public class FlowModelDto {

  @ApiModelProperty(value = "流程表单模板部门名称", required = true)
  private String formDept;

//    @ApiModelProperty(value = "流程表单模板摘要", required = false)
//    private List<AbstractDto> formAbstract;

  @ApiModelProperty(value = "流程表单模板名称(唯一值)", required = true)
  private String formName;

  @ApiModelProperty(value = "流程表单模板key(唯一值)", required = true)
  private String formKey;

  @ApiModelProperty(value = "表单模板", required = true)
  private Map formModelJson;

  @ApiModelProperty(value = "表单条件分支")
  private String formBranchConditions;

  @ApiModelProperty(value = "更新表单模板的方式：all-表单和条件、可编辑节点等全部更新，onlyForm-仅更新表单，其他属性不变")
  private String updateType;

  @ApiModelProperty(value = "需要从表单获取流程节点审批人")
  private String nodeApproveUsers;

  @ApiModelProperty("表单状态：0-未启用-默认；1-启用；2-草稿")
  private Integer flowStatus;

  @ApiModelProperty("表单特殊字段集合（hide:隐藏;edit:可编辑）")
  private List<Map<String, Object>> editNodeName;

  public void setFormModelJson(Object formModelJson) {
    this.formModelJson = CommonUtil.o2m(formModelJson);
  }

  public FlowModelDto() {
  }

  public FlowModelDto(FlowModel flowModel) {
    formDept = flowModel.getFormDept();
    formName = flowModel.getFormName();
    formKey = flowModel.getFormKey();
    formModelJson = CommonUtil.s2m(flowModel.getFormModels());
  }

  public FlowModelDto(FlowModel flowModel, List<Map<String, Object>> editNodeName) {
    formDept = flowModel.getFormDept();
    formName = flowModel.getFormName();
    formKey = flowModel.getFormKey();
    this.editNodeName = editNodeName;
    formModelJson = CommonUtil.s2m(flowModel.getFormModels());
    formBranchConditions = flowModel.getFormBranchConditions();
    nodeApproveUsers = flowModel.getNodeApproveUsers();
  }


}
