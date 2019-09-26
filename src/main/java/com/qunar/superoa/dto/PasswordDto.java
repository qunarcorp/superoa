package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction: 修改密码
 * @Date:Created in 10:14 AM 2019/5/30
 * @Modify by:
 */
@Data
public class PasswordDto {

  @ApiModelProperty("用户id")
  private String id;

  @ApiModelProperty("用户名")
  private String userName;

  @ApiModelProperty("旧密码")
  private String oldPassword;

  @ApiModelProperty("新密码")
  private String newPassword;

  @ApiModelProperty("确认密码")
  private String confirmPassword;

}
