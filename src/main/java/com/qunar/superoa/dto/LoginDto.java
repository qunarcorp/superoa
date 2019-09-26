package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/13_下午7:45
 * @Despriction:
 */

@Data
public class LoginDto {

    @ApiModelProperty("用户名")
    private String qtalk;

    @ApiModelProperty("密码")
    private String password;
}
