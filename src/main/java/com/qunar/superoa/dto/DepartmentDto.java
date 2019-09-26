package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction: 添加/更新部门
 * @Date:Created in 3:34 PM 2019/5/21
 * @Modify by:
 */
@Data
public class DepartmentDto {

  @ApiModelProperty(value = "部门id")
  private String id;

  @NotNull
  @ApiModelProperty(value = "部门名称")
  private String name;

  @ApiModelProperty(value = "部门vp")
  private String vp = "";

  @ApiModelProperty(value = "部门领导")
  private String leaders = "";

  @ApiModelProperty(value = "部门hrbps")
  private String hrbps = "";

  @ApiModelProperty(value = "部门层级")
  private Integer deep;

  @ApiModelProperty(value = "上级部门id")
  private String pid;

  @ApiModelProperty(value = "以'/'连接的一级部门到当前部门")
  private String nodeNameStr;

  @ApiModelProperty(value = "部门人数")
  private Integer userSize;

  @ApiModelProperty(value = "所有用户信息")
  private List usersInfo;

}
