package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 16:15 2018/9/6
 */

@Data
public class RoleDto {

    @ApiModelProperty("权限（SYSTEM_ADMIN：系统管理员; ACTIVITI_ADMIN: 流程管理员; ROLE_USER：普通用户）")
    private String role;

    public RoleDto (String role) {
        super();
        this.role = role;
    }

    public RoleDto (){
        super();
    }
}
