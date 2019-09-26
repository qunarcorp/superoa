package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by xing.zhou on 2018/8/30.
 */
@Data
@Entity
@Table(indexes = {@Index(name = "idx_procInstId", columnList = "procInstId")})
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@ApiModel("表单数据")
public class FormData {


    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty(value = "表单数据id", required = false)
    private String id;

//    @ApiModelProperty(value = "流程表单模板摘要", required = false)
//    private String formAbstract;

    @ApiModelProperty(value = "流程Id", required = true)
    private String procInstId;

    @NotNull(message = "表单模板id不能为空")
    @ApiModelProperty(value = "表单模板id", required = true)
    private String formModelId;

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

    @NotNull(message = "版本号不能为空")
    @ApiModelProperty(value = "版本号", required = true)
    private String formVersion;

    @NotNull(message = "修改时间不能为空")
    @ApiModelProperty(value = "修改时间", required = true)
    private String updateTime;
}
