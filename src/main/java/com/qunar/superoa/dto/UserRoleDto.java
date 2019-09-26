package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 16:04 2018/9/7
 */

@Data
public class UserRoleDto {

  @ApiModelProperty("用户名称")
  private String username;

  @ApiModelProperty("角色")
  private String role;

  @ApiModelProperty("角色名称")
  private String roleName;

  public UserRoleDto(String username, String role) {
    super();
    this.username = username;
    this.role = role;
  }

  public UserRoleDto(String username, String role, String roleName) {
    super();
    this.username = username;
    this.role = role;
    this.roleName = roleName;
  }

  public UserRoleDto() {
    super();
  }
}
