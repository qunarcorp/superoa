package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Created by xing.zhou on 2018/9/13.
 */
@Data
public class DeptDto {

  @ApiModelProperty("流程所属部门")
  private String dept;

  @ApiModelProperty("流程表单模板名称")
  private String formName;

  @ApiModelProperty("流程表单模板key")
  private String formKey;

  @ApiModelProperty("流程表单状态")
  private Integer flowStatus;

  @ApiModelProperty("当前页， 默认1")
  private int page = 1;

  @ApiModelProperty("当前页, 默认10")
  private int size = 10;

  @ApiModelProperty("当前页, 默认根据id")
  private String sort = "updateTime";

  @ApiModelProperty("排序方式(DESC/ASC) 默认DESC")
  private String direction = "DESC";


  public PageRequest getPageAble() {
    return PageRequest.of(
        page < 1 ? 0 : page - 1,
        size == 0 ? 6 : size,
        new Sort(
            "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
            "".endsWith(sort) ? "id" : sort
        )
    );
  }
}
