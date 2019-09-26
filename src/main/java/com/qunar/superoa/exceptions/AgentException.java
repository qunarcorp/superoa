package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_下午2:53
 * @Despriction:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class AgentException extends RuntimeException{

    private Integer code;

    public AgentException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }
}
