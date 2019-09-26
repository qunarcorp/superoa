package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/27_3:53 PM
 * @Despriction: 登录账号信息
 */

@Data
public class Account {

    @ApiModelProperty("登录类型")
    private String type;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("权限")
    private List<String> currentAuthority;
}
