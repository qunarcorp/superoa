package com.qunar.superoa.exceptions;

import com.qunar.superoa.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 4:02 PM 2019/3/19
 * @Modify by:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EncryptException extends RuntimeException {

  private Integer code;

  public EncryptException(ResultEnum resultEnum) {
    super(resultEnum.getMsg());
    this.code = resultEnum.getCode();
  }

}
