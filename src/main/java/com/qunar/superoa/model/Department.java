package com.qunar.superoa.model;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 上午11:37 2018/10/29
 * @Modify by:
 */

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
 * 员工各级部门
 */
@Data
@Entity
@Table(indexes = {
    @Index(columnList = "pid", name = "idx_pid")
})
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@ApiModel("员工各级部门")
public class Department {

  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  @ApiModelProperty("部门Id")
  private String id;

  @ApiModelProperty(value = "部门分管领导")
  private String vp;

  @ApiModelProperty(value = "以'$'连接一级部门到父部门的id")
  private String parentStr;

  @ApiModelProperty(value = "以','连接的直接领导信息")
  private String leaders;

  @ApiModelProperty(value = "以'/'连接的一级部门到当前部门")
  private String nodeNameStr;

  @ApiModelProperty(value = "上一级部门id")
  private String pid;

  @ApiModelProperty(value = "当前部门的级数，1代表一级部门，2代表二级部门，以此类推")
  private int deep;

  @ApiModelProperty(value = "以','连接的hrbp信息")
  private String hrbps;

  @ApiModelProperty(value = "部门名称")
  private String name;

}
