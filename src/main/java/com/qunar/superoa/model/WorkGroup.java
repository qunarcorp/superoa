package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Lob;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_上午10:21
 * @Despriction: 工作组
 */

@Data
@Entity
@ApiModel("工作组")
public class WorkGroup {

  /**
   * ID
   */
  @Id
  @GeneratedValue
  @ApiModelProperty(value = "工作组ID")
  private Integer id;

  /**
   * 工作组名称
   */
  @NotNull(message = "组名称必填")
  @ApiModelProperty(value = "工作组名字", required = true)
  private String name;

  /**
   * 工作组成员
   */
  @ApiModelProperty(value = "工作组成员，逗号分隔")
  private String members;

  /**
   * 备注
   */
  @ApiModelProperty(value = "备注")
  private String remarks;
}
