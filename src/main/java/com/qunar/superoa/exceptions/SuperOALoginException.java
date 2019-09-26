package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_下午3:53
 * @Despriction:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SuperOALoginException extends RuntimeException {

    private Integer code;

    public SuperOALoginException (ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public SuperOALoginException (Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
