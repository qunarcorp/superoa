package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by xing.zhou on 2018/8/21.
 */
@Data
@Entity
@Table(indexes = {@Index(name = "idx_oid", columnList = "oid")})
@ApiModel("审批日志")
@GenericGenerator(name="jpa-uuid", strategy = "uuid")
public class ApproveLog {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty(value = "审批日志ID")
    private String id;

    @NotNull(message = "oid不能为空")
    @ApiModelProperty(value = "oid", required = true)
    private String oid;

    @NotNull(message = "otype不能为空")
    @ApiModelProperty(value = "流程类型", required = true)
    private String otype;

    @ApiModelProperty(value = "审批人qtalk")
    private String approveUserId;

    @ApiModelProperty(value = "审批人姓名")
    private String approveUserName;

    @ApiModelProperty(value = "节点Name")
    private String taskName;

    @ApiModelProperty(value = "任务节点id")
    private String taskId;

    @NotNull(message = "managerType不能为空")
    @ApiModelProperty(value = "审批类型", required = true)
    private String managerType;

    @NotNull(message = "memo不能为空")
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "审批意见", required = true)
    private String memo;

    @ApiModelProperty(value = "下一节点Name")
    private String nextTaskName;

    @ApiModelProperty(value = "下一节点操作人")
    private String nextCandidate;

    @ApiModelProperty(value = "nodeDefKey")
    private String nodeDefKey;

    @NotNull(message = "approveTime不能为空")
    @ApiModelProperty(value = "审批时间", required = true)
    private String approveTime;
}
