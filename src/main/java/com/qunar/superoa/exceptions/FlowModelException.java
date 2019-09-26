package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: xing.zhou
 * @Auther: lee.guo
 * @Date:Created in 2018/9/29_2:23 PM
 * @Despriction:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class FlowModelException extends RuntimeException {

  private Integer code;

  public FlowModelException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }

  public FlowModelException(ResultEnum resultEnum, String message) {
    super(resultEnum.getMsg() + "---" + message);
    this.code = resultEnum.getCode();
  }
}
