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
 * @Despriction:
 * @Date:Created in 5:27 PM 2019/4/29
 * @Modify by:
 */
@Data
@Entity
@Table(indexes = {@Index(name = "idx_oid", columnList = "oid")})
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@ApiModel("集成系统可发起流程实例")
public class ExtSysFlowModel {
  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  @ApiModelProperty(value = "流程实例Id")
  private String id;

  @ApiModelProperty(value = "流程所属系统标识")
  private String processKeys;

  @ApiModelProperty(value = "流程对应原系统中的id")
  private String oid;

  @ApiModelProperty(value = "可发起流程标题")
  private String title;

  @ApiModelProperty(value = "发起流程跳转链接")
  private String redirectUrl;

  @ApiModelProperty(value = "流程是否属于外部系统")
  private String formMark;

}
