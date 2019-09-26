package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @author zhouxing
 * @date 2019-03-10 15:17
 */
@Data
@Entity
@ApiModel("acl限制")
public class ACLManage {

  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  @ApiModelProperty("ID")
  private String id;

  @NotNull(message = "ip")
  @ApiModelProperty(value = "ips", required = true)
  private String ips;

  @NotNull(message = "apis")
  @ApiModelProperty(value = "apis", required = true)
  private String apis;

  @NotNull(message = "flows")
  @ApiModelProperty(value = "flows", required = true)
  private String flows;

  @ApiModelProperty(value = "负责人", required = true)
  private String users;

  @ApiModelProperty(value = "描述")
  private String remark;

}
