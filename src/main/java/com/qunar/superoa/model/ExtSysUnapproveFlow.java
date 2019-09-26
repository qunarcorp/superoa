package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * @Auther: chengyan.liang
 * @Despriction: 集成系统待审批流程
 * @Date:Created in 3:40 PM 2019/4/26
 * @Modify by:
 */
@Data
@Entity
@Table(indexes = {@Index(name = "idx_oid", columnList = "oid")})
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@ApiModel("集成系统待审批流程实例")
public class ExtSysUnapproveFlow {

  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  @ApiModelProperty(value = "流程实例Id")
  private String id;

  @ApiModelProperty(value = "流程所属系统标识")
  private String processKeys;

  @ApiModelProperty(value = "流程对应原系统中的id")
  private String oid;

  @ApiModelProperty(value = "申请人")
  private String applyUser;

  @Column(columnDefinition = "TEXT")
  @ApiModelProperty(value = "待审批人，以英文逗号分隔")
  private String approveUsers;

  @ApiModelProperty(value = "流程审批标题")
  private String title;

  @ApiModelProperty(value = "申请时间")
  private String applyTime;

  @ApiModelProperty(value = "流程最新更新时间")
  private String updateTime;

  @ApiModelProperty(value = "流程跳转链接")
  private String redirectUrl;

}
