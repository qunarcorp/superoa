package com.qunar.superoa.model;

import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @Auther: lee.guo
 * @Despriction: 推送到opsapp的数据
 * @Date: Created in 6:06 PM 2018/10/16
 * @Modify by:
 */
@Data
public class OpsappHistoryData {

  @ApiModelProperty("ID")
  private String oid;

  @ApiModelProperty("申请人")
  private String user;

  @ApiModelProperty("审批人")
  private String approver;

  @ApiModelProperty("本次审批结果  1 通过 0 拒绝  -1 其他 必选项")
  private String result;

  @ApiModelProperty("如果有下一节点，下一节点审批人， 逗号分隔，如果没有下一节点置空")
  private String next;

  @ApiModelProperty("\"%Y-%m-%d %H:%M:%S\" 格式字符串")
  private String update_time;

  @ApiModelProperty("\"%Y-%m-%d %H:%M:%S\" 格式字符串")
  private String create_time;

  @ApiModelProperty("标题")
  private String sum;

  @ApiModelProperty("其他扩展信息，json字符串，没用可为空")
  private String ext;

  public OpsappHistoryData() {
  }

  public OpsappHistoryData(FlowOrder flowOrder, String result, String currentUser) {
    this.oid = flowOrder.getId();
    this.user = flowOrder.getApplyUserId();
    this.approver = currentUser;
    this.result = result;
    this.next = flowOrder.getNextApproveUsers();
    this.update_time = flowOrder.getUpdateTime();
    this.create_time = flowOrder.getApplyTime();
    this.sum = flowOrder.getHeadline();
    this.ext = "";
  }
}
