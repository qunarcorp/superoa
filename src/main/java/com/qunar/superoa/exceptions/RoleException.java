package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 10:30 2018/9/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleException extends RuntimeException {

    private Integer code;

    public RoleException (ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public RoleException (Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
