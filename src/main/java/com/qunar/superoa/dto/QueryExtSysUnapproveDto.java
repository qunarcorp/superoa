package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Auther: chengyan.liang
 * @Despriction: 查询外部系统审批流程参数传递
 * @Date:Created in 4:49 PM 2019/4/28
 * @Modify by:
 */
@Data
public class QueryExtSysUnapproveDto {

  @ApiModelProperty(value = "关键字搜索字段")
  private String key;

  @ApiModelProperty(value = "外部系统标识")
  private String flowType;

  @ApiModelProperty(value = "提交审批开始时间")
  private String submitBeginTime;

  @ApiModelProperty(value = "提交审批结束时间")
  private String submitEndTime;

  @ApiModelProperty(value = "完成审批开始时间")
  private String finishBeginTime;

  @ApiModelProperty(value = "完成审批结束时间")
  private String finishEndTime;

  @ApiModelProperty(value = "发起人")
  private String userId;

  @ApiModelProperty(value = "查询类型(0:我发起的; 2:待我审批的)")
  private String queryType;

  @ApiModelProperty("当前页，默认1")
  private int page = 1;

  @ApiModelProperty("页容量，默认10")
  private int size = 10;

  @ApiModelProperty("排序方式,默认根据流程提交时间")
  private String sort = "applyTime";

  @ApiModelProperty("排序方式(DESC/ASC) 默认DESC")
  private String direction = "DESC";

  public PageRequest getPageAble() {
    return PageRequest.of(
        page < 1 ? 0 : page -1,
        size == 0 ? 10 :size,
        new Sort(
            "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
            "".endsWith(sort) ? "oid" : sort
        )
    );
  }
}
