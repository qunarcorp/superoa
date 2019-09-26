package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 2:05 PM 2019/4/30
 * @Modify by:
 */
@Data
public class ExtSysDto {

  @ApiModelProperty(value = "外部系统传输数据")
  private Object dataSource;

}
