package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:38
 * @Despriction:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class GirlException extends RuntimeException{

    private Integer code;

    public GirlException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }
}
