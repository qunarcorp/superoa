package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:57 AM 2019/5/22
 * @Modify by:
 */
@Data
public class OAUserDto {

  /**
   * 用户id
   */
  @ApiModelProperty("用户id")
  private String id;

  /**
   * userName,唯一
   */
  @ApiModelProperty("userName")
  private String userName;

  /**
   * 用户密码
   */
  @ApiModelProperty("密码")
  private String password;

  /**
   * 中文名
   */
  @ApiModelProperty("中文名")
  private String cname;

  /**
   * 用户所属部门id
   */
  @ApiModelProperty("部门id")
  private String deptId;

  /**
   * 用户所属部门
   */
  @ApiModelProperty("用户所属部门")
  private String deptStr;

  /**
   * 用户直属领导
   */
  @ApiModelProperty("用户直属领导")
  private String leader;

  /**
   * 用户负责hr
   */
  @ApiModelProperty("用户负责hr")
  private String hr;

  /**
   * 头像地址
   */
  @ApiModelProperty("头像地址")
  private String avatar;

  /**
   * 性别
   */
  @ApiModelProperty("性别 - 0:女; 1:男")
  private String gender;

  /**
   * 个人心情签名
   */
  @ApiModelProperty("个人心情签名")
  private String mood;

  /**
   * 个人邮箱
   */
  @ApiModelProperty("个人邮箱")
  private String email;

  /**
   * 手机号
   */
  @ApiModelProperty("手机号")
  private String phone;

}
