package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午5:32 2018/9/29
 * @Modify by:
 */
@Data
@ApiModel("流程实例")
public class FlowOrder {

  @ApiModelProperty(value = "流程实例ID")
  private String id;

  @NotNull(message = "applyUserId不能为空")
  @ApiModelProperty(value = "申请人Id", required = true)
  private String applyUserId;

  @NotNull(message = "applyUserDept不能为空")
  @ApiModelProperty(value = "申请人部门", required = true)
  private String applyUserDept;

  @NotNull(message = "applyUserName不能为空")
  @ApiModelProperty(value = "申请人Name", required = true)
  private String applyUserName;

  @ApiModelProperty(value = "申请人头像url")
  private String applyUserAvatar;

//    @ApiModelProperty(value = "摘要")
//    private String abstracts;

  @NotNull(message = "标题不能为空")
  @ApiModelProperty(value = "标题", required = true)
  private String headline;

  @NotNull(message = "applyTypeKey不能为空")
  @ApiModelProperty(value = "流程类型key", required = true)
  private String applyTypeKey;

  @NotNull(message = "applyTypeName不能为空")
  @ApiModelProperty(value = "流程类型Name", required = true)
  private String applyTypeName;

  @NotNull(message = "applyTime不能为空")
  @ApiModelProperty(value = "申请时间", required = true)
  private String applyTime;

  @ApiModelProperty(value = "审批完成时间")
  private String lastApproveTime;

  @ApiModelProperty(value = "最后审批人")
  private String lastApproveUserId;


  @ApiModelProperty(value = "下一审批人列表")
  private String nextApproveUsers;

  @ApiModelProperty(value = "已审批人列表")
  private String approvedUsers;

  @ApiModelProperty(value = "表单时间相关 - 提交时间/完成时间")
  private String flowTime;

  @NotNull(message = "applyStatus不能为空")
  @ApiModelProperty(value = "状态(0:已完成;1:待审批;2:撤销;3:草稿;5:拒绝)", required = true)
  private int applyStatus;

  @NotNull(message = "procInstId不能为空")
  @ApiModelProperty(value = "流程Id", required = true)
  private String procInstId;

  @NotNull(message = "logOid不能为空")
  @ApiModelProperty(value = "流程日志Oid", required = true)
  private String logOid;

  @NotNull(message = "修改时间不能为空")
  @ApiModelProperty(value = "修改时间", required = true)
  private String updateTime;

  public FlowOrder(){}

  public FlowOrder(String id,
      @NotNull(message = "applyUserId不能为空") String applyUserId,
      @NotNull(message = "applyUserDept不能为空") String applyUserDept,
      @NotNull(message = "applyUserName不能为空") String applyUserName, String applyUserAvatar,
      @NotNull(message = "标题不能为空") String headline,
      @NotNull(message = "applyTypeKey不能为空") String applyTypeKey,
      @NotNull(message = "applyTypeName不能为空") String applyTypeName,
      @NotNull(message = "applyTime不能为空") String applyTime, String lastApproveTime,
      String lastApproveUserId, String lastApproveUserName, String nextApproveUsers,
      String approvedUsers,
      @NotNull(message = "applyStatus不能为空") int applyStatus,
      @NotNull(message = "procInstId不能为空") String procInstId,
      @NotNull(message = "logOid不能为空") String logOid,
      @NotNull(message = "修改时间不能为空") String updateTime) {
    this.id = id;
    this.applyUserId = applyUserId;
    this.applyUserDept = applyUserDept;
    this.applyUserName = applyUserName;
    this.applyUserAvatar = applyUserAvatar;
    this.headline = headline;
    this.applyTypeKey = applyTypeKey;
    this.applyTypeName = applyTypeName;
    this.applyTime = applyTime;
    this.lastApproveTime = lastApproveTime;
    this.lastApproveUserId = lastApproveUserId;
    this.nextApproveUsers = nextApproveUsers;
    this.approvedUsers = approvedUsers;
    this.applyStatus = applyStatus;
    this.procInstId = procInstId;
    this.logOid = logOid;
    this.updateTime = updateTime;
  }

  public FlowOrder(com.qunar.superoa.model.FlowOrder flowOrder, String avatar) {
    this.id = flowOrder.getId();
    this.applyUserId = flowOrder.getApplyUserId();
    this.applyUserDept = flowOrder.getApplyUserDept();
    this.applyUserName = flowOrder.getApplyUserName();
    this.headline = flowOrder.getHeadline();
    this.applyTypeKey = flowOrder.getApplyTypeKey();
    this.applyTypeName = flowOrder.getApplyTypeName();
    this.applyTime = flowOrder.getApplyTime();
    this.lastApproveTime = flowOrder.getLastApproveTime();
    this.lastApproveUserId = flowOrder.getLastApproveUserId();
    this.nextApproveUsers = flowOrder.getNextApproveUsers();
    this.approvedUsers = flowOrder.getApprovedUsers();
    this.applyStatus = flowOrder.getApplyStatus();
    this.procInstId = flowOrder.getProcInstId();
    this.logOid = flowOrder.getLogOid();
    this.updateTime = flowOrder.getUpdateTime();
    this.applyUserAvatar = avatar;
    this.flowTime = flowOrder.getApplyTime() + "//" + flowOrder.getLastApproveTime();
  }
}
