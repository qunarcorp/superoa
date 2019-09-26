package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 10:42 PM 2018/12/21
 * @Modify by:
 */
@Data
@ApiModel("发送qtalk消息")
public class QtalkMessageBody {

  @ApiModelProperty("")
  private String auto_reply;
  @ApiModelProperty("")
  private String backupinfo;
  @ApiModelProperty("")
  private String content;
  @ApiModelProperty("")
  private String extendinfo;
  @ApiModelProperty("发送方")
  private String from;
  @ApiModelProperty("")
  private String fromhost;
  @ApiModelProperty("")
  private String msgtype;
  @ApiModelProperty("")
  private String system;
  @ApiModelProperty("")
  private List<Map<String, String>> to;
  @ApiModelProperty("")
  private String type;

  public QtalkMessageBody(String MucId, String userName) {
    List<Map<String, String>> toList = new ArrayList();
    Map<String, String> toMap = new HashMap();
    toMap.put("user", MucId);
    toMap.put("host", "conference.ejabhost1");
    toList.add(toMap);
    this.from = "worknotice";
    this.to = toList;
    this.type = "groupchat";
    this.extendinfo = "";
    this.msgtype = "1";
    this.content = "@" + userName + " 同学你好, QOA系统相关问题请咨询 @郭立lee @周星 @粱成琰 , 流程相关问题请咨询 @蒋卫东 。";
    this.system = "ops_superoa";
    this.auto_reply = "false";
    this.backupinfo = "";
    this.fromhost = "ejabhost1";
  }


}
