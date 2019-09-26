package com.qunar.superoa.model;

import io.swagger.annotations.ApiParam;
import java.io.Serializable;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_6:35 PM
 * @Despriction: 用户角色表
 */

@Data
@Entity
public class UserRole implements Serializable {

  /**
   * ID
   */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /**
   * 用户Qtalk
   */
  @NotNull(message = "用户名不能为空")
  @Column(name = "qtalk", nullable = false)
  @ApiParam(value = "用户Qtalk", required = true)
  private String qtalk;

  /**
   * 角色类型
   */
  @NotNull(message = "角色类型不能为空")
  @ApiParam(value = "角色类型", required = true)
  private String roleType;

  /**
   * 角色描述
   */
  @ApiParam(value = "角色描述", required = true)
  private String roleName;

}
