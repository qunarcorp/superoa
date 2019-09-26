package com.qunar.superoa.model;

import com.qunar.superoa.utils.DateTimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_上午11:59
 * @Despriction: 代理人
 */
@Data
@Entity
@ApiModel("代理人")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Agent {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty("代理人ID")
    private String id;

    @NotNull(message = "Qtalk(被代理人)不能为空")
    @ApiModelProperty(value = "Qtalk", required = true)
    private String qtalk;

    @NotNull(message = "代理人不能为空")
    @ApiModelProperty(value = "代理人Qtalk(多个用逗号分隔)", required = true)
    private String agent;

    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "代理截止时间(yyyy-MM-dd HH:mm:ss)")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    @ApiModelProperty(value = "代理流程ID(默认全部)")
    private String processID;

    @ApiModelProperty(value = "更新时间(参数不需要传入)")
    private String updateTime;

    @ApiModelProperty("备注")
    private String remarks;

    public String getDeadline(){
        if(this.deadline == null) return null;
        return DateTimeUtil.toString(this.deadline);
    }

    public void setDeadline(String date){
        setDeadline(DateTimeUtil.toDate(date));
    }

    public void setDeadline(Date date){
        this.deadline = date;
    }
}
