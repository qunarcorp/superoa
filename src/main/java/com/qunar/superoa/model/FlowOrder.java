package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by xing.zhou on 2018/8/22.
 */
@Data
@Entity
@Table(indexes = {
    @Index(name = "idx_procInstId", columnList = "procInstId"),
    @Index(name = "idx_logOid", columnList = "logOid")})
@GenericGenerator(name="jpa-uuid", strategy = "uuid")
@ApiModel("流程实例")
public class FlowOrder {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty(value = "流程实例ID")
    private String id;

    @NotNull(message = "applyUserId不能为空")
    @ApiModelProperty(value = "申请人Id", required = true)
    private String applyUserId;

    @NotNull(message = "applyUserDept不能为空")
    @ApiModelProperty(value = "申请人部门", required = true)
    private String applyUserDept;

    @ApiModelProperty( value = "申请人全部部门")
    private String applyUserFullDept;

    @NotNull(message = "applyUserName不能为空")
    @ApiModelProperty(value = "申请人Name", required = true)
    private String applyUserName;

//    @ApiModelProperty(value = "摘要")
//    private String abstracts;

    @NotNull(message = "标题不能为空")
    @ApiModelProperty(value = "标题", required = true)
    private String headline;

    @NotNull(message = "applyTypeKey不能为空")
    @ApiModelProperty(value = "流程类型key", required = true)
    private String applyTypeKey;

    @NotNull(message = "applyTypeName不能为空")
    @ApiModelProperty(value = "流程类型Name", required = true)
    private String applyTypeName;

    @NotNull(message = "applyTime不能为空")
    @ApiModelProperty(value = "申请时间", required = true)
    private String applyTime;

    @ApiModelProperty(value = "审批完成时间")
    private String lastApproveTime;

    @ApiModelProperty(value = "最后审批人")
    private String lastApproveUserId;

    @ApiModelProperty(value = "下一审批人列表")
    @Column(columnDefinition = "TEXT")
    private String nextApproveUsers;

    @ApiModelProperty(value = "已审批人列表")
    @Column(columnDefinition = "TEXT")
    private String approvedUsers;

    @NotNull(message = "applyStatus不能为空")
    @ApiModelProperty(value = "状态(0:已完成;1:待审批;2:撤销;3:草稿;5:拒绝)", required = true)
    private int applyStatus;

    @NotNull(message = "procInstId不能为空")
    @ApiModelProperty(value = "流程Id", required = true)
    private String procInstId;

    @NotNull(message = "logOid不能为空")
    @ApiModelProperty(value = "流程日志Oid", required = true)
    private String logOid;

    @NotNull(message = "修改时间不能为空")
    @ApiModelProperty(value = "修改时间", required = true)
    private String updateTime;

    @ApiModelProperty(value = "可查看人列表")
    @Column(columnDefinition = "TEXT")
    private String queryUsers;

    @ApiModelProperty(value = "知会人列表")
    @Column(columnDefinition = "TEXT")
    private String notifyUsers;

    @ApiModelProperty(value = "appCode")
    private String appCode;

    public FlowOrder(){

    }

    public FlowOrder(String applyUserId){
        this.applyUserId = applyUserId;
    }

    public FlowOrder(String id, String applyUserId, String applyUserDept, String applyUserName, String headline,
                     String applyTypeKey, String applyTypeName, String applyTime, String lastApproveTime, String lastApproveUserId,
                     String nextApproveUsers, String approvedUsers, int applyStatus, String procInstId, String logOid,
                     String updateTime) {
        this.id = id;
        this.applyUserId = applyUserId;
        this.applyUserDept = applyUserDept;
        this.applyUserName = applyUserName;
        this.headline = headline;
        this.applyTypeKey = applyTypeKey;
        this.applyTypeName = applyTypeName;
        this.applyTime = applyTime;
        this.lastApproveTime = lastApproveTime;
        this.lastApproveUserId = lastApproveUserId;
        this.nextApproveUsers = nextApproveUsers;
        this.approvedUsers = approvedUsers;
        this.applyStatus = applyStatus;
        this.procInstId = procInstId;
        this.logOid = logOid;
        this.updateTime = updateTime;
    }

}
