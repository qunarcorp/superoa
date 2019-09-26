package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午7:19
 * @Despriction: 分页对象
 */
@Data
public class PageAble {

  /**
   * 搜索
   */
  @ApiModelProperty("搜索内容")
  private String k;

  @ApiModelProperty("当前页， 默认1")
  private int page = 1;

  @ApiModelProperty("当前页, 默认10")
  private int size = 10;

  @ApiModelProperty("当前页, 默认根据id")
  private String sort = "updateTime";

  @ApiModelProperty("排序方式(DESC/ASC) 默认DESC")
  private String direction = "DESC";

  public PageAble(){}

  public PageRequest getPageAble() {
    return PageRequest.of(page < 1 ? 0 : page - 1,
        size == 0 ? 6 : size,
        new Sort(
            "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
            "".endsWith(sort) ? "id" : sort
        ));
  }
}
