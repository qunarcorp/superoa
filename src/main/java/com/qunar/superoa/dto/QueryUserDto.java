package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 3:22 PM 2019/5/22
 * @Modify by:
 */
@Data
public class QueryUserDto {

  @ApiModelProperty(value = "关键字搜索字段")
  private String key;

  @ApiModelProperty("当前页， 默认1")
  private int page = 1;

  @ApiModelProperty("当前页, 默认10")
  private int size = 10;

  @ApiModelProperty("当前页, 默认根据id")
  private String sort = "userName";

  @ApiModelProperty("排序方式(DESC/ASC) 默认ASC")
  private String direction = "ASC";

  public PageRequest getPageAble() {
    return PageRequest.of(
        page < 1 ? 0 : page - 1,
        size == 0 ? 10 : size,
        new Sort(
            "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
            "".endsWith(sort) ? "userName" : sort
        )
    );
  }

}
