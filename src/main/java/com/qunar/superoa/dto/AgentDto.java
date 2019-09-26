package com.qunar.superoa.dto;

import com.qunar.superoa.model.Agent;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.annotation.Resource;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午3:40
 * @Despriction: 代理人
 */


@Data
@Resource
public class AgentDto {

  @ApiModelProperty("代理人ID")
  private String id;

  @ApiModelProperty(value = "Qtalk", required = true)
  private String qtalk;

  @ApiModelProperty(value = "代理人Qtalk(多个用逗号分隔)", required = true)
  private String agent;

  @ApiModelProperty(value = "代理截止时间(yyyy-MM-dd HH:mm:ss)")
  private String deadline;

  @ApiModelProperty(value = "代理流程ID(默认全部)")
  private String processID;

  @ApiModelProperty(value = "添加时间(yyyy-MM-dd HH:mm:ss)")
  private String updateTime;

  @ApiModelProperty("备注")
  private String remarks;

  @ApiModelProperty(value = "代理流程list")
  private List<FlowModelNameDto> flowModels;

  public AgentDto(Object agentObject) {
    Agent agent = (Agent) agentObject;
    this.id = agent.getId();
    this.qtalk = agent.getQtalk();
    this.agent = agent.getAgent();
    this.deadline = agent.getDeadline();
    this.processID = agent.getProcessID();
    this.updateTime = agent.getUpdateTime();
    this.remarks = agent.getRemarks();
  }

  public AgentDto(String id, String qtalk, String agent, String deadline, String processID,
      String updateTime, String remarks, List<FlowModelNameDto> flowModels) {
    this.id = id;
    this.qtalk = qtalk;
    this.agent = agent;
    this.deadline = deadline;
    this.processID = processID;
    this.updateTime = updateTime;
    this.remarks = remarks;
    this.flowModels = flowModels;
  }

  public AgentDto() {}

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

}
