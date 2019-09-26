package com.qunar.superoa.model;

import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dto.StarTalkUserDto;
import com.qunar.superoa.utils.DateTimeUtil;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/24_7:33 PM
 * @Despriction: 用户表
 */
@Data
@Entity
@Table(indexes = {
    @Index(columnList = "deptId", name = "idx_deptId")
})
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class SuperOAUser implements Serializable {


  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "jpa-uuid")
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
   * 用户密码盐
   */
  @ApiModelProperty("用户密码盐")
  private String salt;

  /**
   * 中文名
   */
  @ApiModelProperty("中文名")
  private String cname;

  /**
   * 员工信息来源
   */
  @ApiModelProperty(value = "员工信息来源 - oa:系统添加; ext:外部系统如startQtalk集成获取")
  private String userFrom = "oa";

  /**
   * 用户所属部门id
   */
  @ApiModelProperty("部门id")
  private String deptId;

  /**
   * 用户直属领导
   */
  @ApiModelProperty("用户直属领导")
  private String leader;

  /**
   * 负责HR
   */
  @ApiModelProperty("负责HR")
  private String hr;

  /**
   * 用户所属部门
   */
  @ApiModelProperty("用户所属部门")
  private String deptStr;

  /**
   * 头像地址
   */
  @Column(columnDefinition = "TEXT")
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
  @Column(columnDefinition = "TEXT")
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

  /**
   * 更新时间
   */
  @ApiModelProperty("更新时间")
  private String updateTime;

  public SuperOAUser() {}

  public SuperOAUser(SuperOAUser superOAUser) {
    this.userName = superOAUser.getUserName();
    this.password = superOAUser.getPassword();
    this.avatar = superOAUser.getAvatar();
    this.gender = superOAUser.getGender();
    this.mood = superOAUser.getMood();
    this.cname = superOAUser.getCname();
    this.deptId = superOAUser.getDeptId();
    this.userFrom = superOAUser.getUserFrom();
    this.email = superOAUser.getEmail();
    this.phone = superOAUser.getPhone();
    this.updateTime = DateTimeUtil.getDate();
    this.leader = superOAUser.getLeader();
    this.salt = superOAUser.getSalt();
  }

  public SuperOAUser(StarTalkUserDto starTalkUserDto) {
    String qtalkAvatorUrl = starTalkUserDto.getAvatar();
    this.avatar = qtalkAvatorUrl.isEmpty()
        ? Constant.OA_USER_AVATAR
        : !qtalkAvatorUrl.contains("qunar.com")
        ? Constant.QT_USER_PREIX + qtalkAvatorUrl
        : qtalkAvatorUrl;
    this.userName = starTalkUserDto.getQtalk();
    this.gender = starTalkUserDto.getGender();
    this.mood = starTalkUserDto.getMood();
    this.updateTime = DateTimeUtil.getDate();
    this.cname = starTalkUserDto.getCname();
  }

}
