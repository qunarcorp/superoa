package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @Auther: xing.zhou
 * @Auther: lee.guo
 * @Date:Created in 2018/9/29_5:06 PM
 * @Despriction:
 */

@Data
public class QueryFlowDto {

  @ApiModelProperty(value = "流程模板名称", required = true)
  private String flowType;

  @ApiModelProperty(value = "流程状态(0:审批完成;1:审批中;2:已撤销;3:未发起;4:全部;5:拒绝)", required = true)
  private String status;

  @ApiModelProperty(value = "关键字搜索字段")
  private String key;

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

  @ApiModelProperty(value = "查询类型(0:我发起的;1:我已审批;2:待我审批;3:知会给我的)")
  private String queryType;

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
