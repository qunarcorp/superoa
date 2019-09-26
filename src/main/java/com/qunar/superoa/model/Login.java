package com.qunar.superoa.model;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/27_4:11 PM
 * @Despriction:
 */

@Data
public class Login {

    @ApiParam(value = "userName", name = "账户", required = true)
    private String userName;

    @ApiParam(value = "password", name = "密码", required = true)
    private String password;

    @ApiParam(value = "type", name = "登录类型", required = true)
    private String type;
}
