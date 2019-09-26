package com.qunar.superoa.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import lombok.Data;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.common.util.DateToStringSerializer;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 11:21 AM 2019/3/26
 * @Modify by:
 */
@Data
public class DeploymentResponse {

  private String id;
  private String name;
  @JsonSerialize(using = DateToStringSerializer.class, as= Date.class)
  private Date deploymentTime;
  private String category;
  private String tenantId;

  public DeploymentResponse(Deployment deployment) {
    setId(deployment.getId());
    setName(deployment.getName());
    setDeploymentTime(deployment.getDeploymentTime());
    setCategory(deployment.getCategory());
    setTenantId(deployment.getTenantId());
  }

}
