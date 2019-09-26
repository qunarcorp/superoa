package com.qunar.superoa.enums;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 10:30 2018/9/3
 */
@Getter
public enum RoleTypeEnum {

    ROLE_SYSTEM_ADMIN(0, "SYSTEM_ADMIN", "系统管理员"),
    ROLE_ACTIVITI_ADMIN(1, "ACTIVITI_ADMIN", "流程管理员");

    int code;
    String text;
    String desc;

    RoleTypeEnum(int code, String text, String desc){
        this.code = code;
        this.text = text;
        this.desc = desc;
    }

    public static RoleTypeEnum getEnumByCode(int code) {
        Preconditions.checkArgument(code >= 0 && code <= 1, "code只能为0-1");
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
            if (roleTypeEnum.code == code) {
                return roleTypeEnum;
            }
        }
        return null;
    }

    public static RoleTypeEnum getEnumByText(String text) {
        Preconditions.checkArgument(!StringUtils.isEmpty(text), "名称不能为空");
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
            if (roleTypeEnum.text.equals(text)) {
                return roleTypeEnum;
            }
        }
        return null;
    }

    public static RoleTypeEnum getEnumByDesc(String desc) {
        Preconditions.checkArgument(!StringUtils.isEmpty(desc), "描述不能为空");
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
            if (roleTypeEnum.desc.equals(desc)) {
                return roleTypeEnum;
            }
        }
        return null;
    }
}
