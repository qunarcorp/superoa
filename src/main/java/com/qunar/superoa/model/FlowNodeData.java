package com.qunar.superoa.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * 用于存储流程所有节点的当前数据
 */
@Data
@Entity
@ApiModel("节点数据表")
@GenericGenerator(name="jpa-uuid", strategy = "uuid")
public class FlowNodeData {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty(value = "节点数据表ID")
    private String id;

    @NotNull(message = "节点key不能为空")
    @ApiModelProperty(value = "节点key")
    private String nodeDefKey;

    @NotNull(message = "节点名称不能为空")
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    @NotNull(message = "操作人Id不能为空")
    @ApiModelProperty(value = "操作人Id")
    private String createUserId;

    @NotNull(message = "操作人Name不能为空")
    @ApiModelProperty(value = "操作人Name")
    private String createUserName;

    @NotNull(message = "流程Id不能为空")
    @ApiModelProperty(value = "流程Id", required = true)
    private String procInstId;

    @NotNull(message = "表单模板name不能为空")
    @ApiModelProperty(value = "表单模板name", required = true)
    private String formModelName;

    @NotNull(message = "表单模板流程key不能为空")
    @ApiModelProperty(value = "表单模板流程key", required = true)
    private String formModelFlowKey;

    @NotNull(message = "表单数据不能为空")
    @ApiModelProperty(value = "表单数据", required = true)
    @Column(columnDefinition = "TEXT")
    private String formDatas;

    @NotNull(message = "创建时间不能为空")
    @ApiModelProperty(value = "创建时间", required = true)
    private String createTime;
}
