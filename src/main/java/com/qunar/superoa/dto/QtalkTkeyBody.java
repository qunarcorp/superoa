package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 10:42 PM 2018/12/21
 * @Modify by:
 */
@Data
@ApiModel("发送qtalk消息")
public class QtalkTkeyBody {

  @ApiModelProperty("appcode")
  private String app_code;
  @ApiModelProperty("ckey")
  private String ckey;

  public QtalkTkeyBody(String ckey) {
    this.app_code = "ops_superoa";
    this.ckey = ckey;
  }


}
