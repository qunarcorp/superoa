package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 5:16 PM 2018/12/21
 * @Modify by:
 */
@Data
@Slf4j
@ApiModel("拉入群的参数")
public class QtalkAddMucUserBody {

  @ApiModelProperty("qtalk域")
  private String muc_domain;

  @ApiModelProperty("群ID")
  private String muc_id;

  @ApiModelProperty("成员")
  private List<String> muc_member;

  @ApiModelProperty("操作人")
  private String muc_owner;

  @ApiModelProperty("appcode")
  private String system;

  public QtalkAddMucUserBody(String muc_id, String muc_owner, String muc_domain, String muc_member,
      String system) {
    List<String> memberList = new ArrayList<>();
    memberList.add(muc_member);
    this.muc_id = muc_id;
    this.muc_domain = muc_domain;
    this.muc_owner = muc_owner;
    this.muc_member = memberList;
    this.system = system;
  }


}
