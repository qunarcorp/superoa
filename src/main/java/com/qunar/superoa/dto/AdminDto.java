package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:49 AM 2019/6/3
 * @Modify by:
 */
@Data
public class AdminDto {

  @ApiModelProperty("用户名")
  private String userName;

  @ApiModelProperty("角色列表")
  private List<String> roles;
}
