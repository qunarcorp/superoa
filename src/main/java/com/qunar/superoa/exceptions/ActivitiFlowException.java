package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:38
 * @Despriction: activities的异常
 */

//避免给继承的方法属性生成get set方法
@EqualsAndHashCode(callSuper = true)
@Data
public class ActivitiFlowException extends RuntimeException {

  private Integer code;

  public ActivitiFlowException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }
}
