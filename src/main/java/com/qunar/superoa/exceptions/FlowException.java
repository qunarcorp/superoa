package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: xing.zhou
 * @Auther: lee.guo
 * @Date:Created in 2018/9/29_2:22 PM
 * @Despriction:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class FlowException extends RuntimeException {

  private Integer code;

  public FlowException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }
}
