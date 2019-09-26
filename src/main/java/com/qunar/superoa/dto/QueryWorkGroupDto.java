package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Created by xing.zhou on 2018/9/18.
 */
@Data
public class QueryWorkGroupDto {

  @ApiModelProperty(value = "工作组名字")
  private String name;

  @ApiModelProperty(value = "工作组成员，逗号分隔")
  private String members;

  @ApiModelProperty(value = "模糊搜索条件")
  private String search;

  @ApiModelProperty("当前页， 默认1")
  private int page = 1;

  @ApiModelProperty("当前页, 默认10")
  private int size = 10;

  @ApiModelProperty("当前页, 默认根据id")
  private String sort = "applyTime";

  @ApiModelProperty("排序方式(DESC/ASC) 默认DESC")
  private String direction = "DESC";

  public PageRequest getPageAble() {
    return PageRequest.of(
        page < 1 ? 0 : page - 1,
        size == 0 ? 10 : size,
        new Sort(
            "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
            "".endsWith(sort) ? "id" : sort
        )
    );
  }
}
