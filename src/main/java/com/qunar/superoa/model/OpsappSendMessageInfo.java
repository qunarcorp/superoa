package com.qunar.superoa.model;

import com.qunar.superoa.constants.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 上午11:58 2018/10/23
 * @Modify by:
 */
@Data
@ApiModel("发送qtalk通知")
public class OpsappSendMessageInfo {

  @ApiModelProperty("发送的人员列表")
  private List qtalk_ids;

  @ApiModelProperty("使用发送的账号")
  private String system;

  @ApiModelProperty("title")
  private String title;

  @ApiModelProperty("content")
  private String content;

  @ApiModelProperty("body")
  private String body;

  @ApiModelProperty("pc端跳转URL")
  private String linkurl;

  @ApiModelProperty("移动端跳转URL")
  private String reacturl;

  @ApiModelProperty("图标url")
  private String img;

  @ApiModelProperty("类型")
  private String type;

  public OpsappSendMessageInfo(){}

  public OpsappSendMessageInfo(com.qunar.superoa.model.FlowOrder flowOrder,String prefix) {
    this.title = prefix + flowOrder.getHeadline();
    this.qtalk_ids = Arrays.asList(flowOrder.getNextApproveUsers().split(","));
    this.system = "worknotice";
    this.img = Constant.QT_NOTICE_IMG;
    this.type = "667";
  }

}
