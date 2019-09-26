package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: xing.zhou
 * @Date:Created in 2019/3/10_3:44 PM
 * @Despriction:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ACLManageException extends RuntimeException {

  private Integer code;

  public ACLManageException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }
}
