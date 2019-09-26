package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by xing.zhou on 2018/8/30.
 */
@Data
@Entity
@Table(indexes = {
    @Index(name = "idx_formKey", columnList = "formKey"),
    @Index(name = "idx_formVersion", columnList = "formVersion"),
    @Index(name = "idx_flowStatus", columnList = "flowStatus")
})
@ApiModel("表单模板")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class FlowModel {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty(value = "表单模板id")
    private String id;

//    @ApiModelProperty(value = "流程表单模板摘要", required = false)
//    private String formAbstract;

    @NotNull(message = "流程表单模板部门名称不能为空")
    @ApiModelProperty(value = "流程表单模板部门名称", required = true)
    private String formDept;

    @NotNull(message = "流程表单模板名称不能为空")
    @ApiModelProperty(value = "流程表单模板名称", required = true)
    private String formName;

    @NotNull(message = "流程表单模板key不能为空")
    @ApiModelProperty(value = "流程表单模板key", required = true)
    private String formKey;

    @ApiModelProperty(value = "流程表单模板节点分支条件(key:k_value,key:v_value)", required = false)
    @Column(columnDefinition = "TEXT")
    private String formBranchConditions;

    @ApiModelProperty(value = "流程表单模板节点审批人选择字段(node:key,node:key)", required = false)
    private String nodeApproveUsers;

    @NotNull(message = "表单数据不能为空")
    @ApiModelProperty(value = "表单数据", required = true)
    @Column(columnDefinition = "TEXT")
    private String formModels;

    @NotNull(message = "版本号不能为空")
    @ApiModelProperty(value = "版本号", required = true)
    private Integer formVersion;

    @NotNull(message = "修改时间不能为空")
    @ApiModelProperty(value = "修改时间", required = true)
    private String updateTime;

    @ApiModelProperty( value = "节点可编辑名称及字段")
    private String editNode;

    @ApiModelProperty( value = "节点属性，包括节点配置人员，节点同一审批人是否跳过")
    private String nodeProperty;

    @NotNull(message = "表单状态码不能为空")
    @ApiModelProperty(value = "表单状态：0-未启用-默认；1-启用；2-草稿", required = true)
    private Integer flowStatus ;
}
