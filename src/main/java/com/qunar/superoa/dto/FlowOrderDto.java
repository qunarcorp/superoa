package com.qunar.superoa.dto;

import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * Created by xing.zhou on 2018/9/12.
 */
@Data
public class FlowOrderDto {


  @ApiModelProperty(value = "流程实例")
  FlowOrder flowOrder;

  @ApiModelProperty(value = "表单模板")
  private Map formModelJson;

  @ApiModelProperty(value = "表单数据")
  private Map formDatas;

  @ApiModelProperty(value = "申请人头像url")
  private String applyUserAvatar;

  public void setFormDatas(Object formDatas) {
    this.formDatas = CommonUtil.o2m(formDatas);
  }

  public void setFormModelJson(Object formModelJson) {
    this.formModelJson = CommonUtil.o2m(formModelJson);
  }
}
