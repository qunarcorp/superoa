package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 10:58 2018/11/30
 * @Modify by:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupException extends RuntimeException {


  private Integer code;

  public GroupException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }
}
