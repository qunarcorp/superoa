package com.qunar.superoa.dto;

import com.qunar.superoa.utils.InvokeUtil;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午5:57
 * @Despriction: 返回带有分页信息的list结果集
 */

@Data
public class PageResult<T> {

  @ApiModelProperty("数据列表")
  private List<T> content;

  @ApiModelProperty("总数")
  private int total;

  @ApiModelProperty("分页信息")
  private Pageable pageable;

  public PageResult() {
  }


  //将model转成dto
  public PageResult(Page<?> pageModel, Class<T> dtoClass) {
    List<T> dto = new ArrayList<>();
    pageModel.getContent().forEach(model -> dto.add((T) InvokeUtil.newDto(model, dtoClass)));
    this.total = (int) pageModel.getTotalElements();
    this.content = dto;
    this.pageable = pageModel.getPageable();
  }

  public PageResult(Page<?> pageModel) {
    this.total = (int) pageModel.getTotalElements();
    this.content = (List<T>) pageModel.getContent();
    this.pageable = pageModel.getPageable();
  }

  public PageResult(Page<?> pageModel, List<?> list) {
    this.total = (int) pageModel.getTotalElements();
    this.content = (List<T>) list;
    this.pageable = pageModel.getPageable();
  }

  public void setContent(Object pageModel, Class<T> dtoClass) {
    List<T> dto = new ArrayList<>();
    getContent().forEach(model -> dto.add((T) InvokeUtil.newDto(model, dtoClass)));
    this.content = dto;
  }
}