package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午6:25
 * @Despriction:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class InvokeException extends RuntimeException {

    private Integer code;

    public InvokeException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }
}
