package com.qunar.superoa.dto;

import com.qunar.superoa.utils.DateTimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.activiti.engine.repository.Model;

/**
 * @Auther: chengyan.liang
 * @Despriction: 流程模型Dto
 * @Date:Created in 7:39 PM 2019/4/2
 * @Modify by:
 */
@Data
public class ModelDto {

  @ApiModelProperty(value = "流程模型Id")
  private String id;

  @ApiModelProperty(value = "流程模型名称")
  private String name;

  @ApiModelProperty(value = "流程模型创建时间")
  private String createTime;

  @ApiModelProperty(value = "流程模型最终修改时间")
  private String lastUpdateTime;

  @ApiModelProperty(value = "流程模型key")
  private String key;

  @ApiModelProperty(value = "流程模型部署Id")
  private String deploymentId;

  @ApiModelProperty(value = "流程模型版本")
  private String version;

  public ModelDto(Model model) {
    this.id = model.getId();
    this.deploymentId = model.getDeploymentId();
    this.key = model.getKey();
    this.name = model.getName();
    this.version = String.valueOf(model.getVersion());
    this.createTime = DateTimeUtil.toString(model.getCreateTime());
    this.lastUpdateTime = DateTimeUtil.toString(model.getLastUpdateTime());
  }

}
