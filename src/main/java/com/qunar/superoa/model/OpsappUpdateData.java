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
public class OpsappUpdateData {

  @ApiModelProperty("ID")
  private String oid;

  @ApiModelProperty("申请人")
  private String user;

  @ApiModelProperty("审批人")
  private List<String> approver;

  @ApiModelProperty("\"%Y-%m-%d %H:%M:%S\" 格式字符串")
  private String time;

  @ApiModelProperty("sum")
  private String sum;

  public OpsappUpdateData() {
  }

  public OpsappUpdateData(FlowOrder flowOrder) {
    this.oid = flowOrder.getId();
    this.user = flowOrder.getApplyUserId();
    this.approver = Arrays.stream(flowOrder.getNextApproveUsers().split(","))
        .filter(v -> StringUtils.isNotBlank(v)).collect(
            Collectors.toList());
    this.time = flowOrder.getApplyTime();
    this.sum = flowOrder.getHeadline();
  }
}
