package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/7_下午6:05
 * @Despriction:
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UploadException extends RuntimeException {


    private Integer code;

    public UploadException(ResultEnum resultEnum){
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

}
