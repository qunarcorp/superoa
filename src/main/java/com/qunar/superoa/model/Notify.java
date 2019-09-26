package com.qunar.superoa.model;

import com.qunar.superoa.utils.DateTimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_下午8:49
 * @Despriction: 通知
 */

@Data
@Entity
@ApiModel("通知")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Notify {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    @ApiModelProperty("通知ID")
    private String id;

    @ApiModelProperty("通知人")
    private String qtalk;

    @ApiModelProperty("通知类型(0:点击跳转待办；  1:点击跳转已申请; -1: 不跳转任何页面; 2:知会; 3:撤销通知待办人; 4:通知其余审批人已被处理)")
    private int noticeType;

    @ApiModelProperty("谁")
    private String who;

    @ApiModelProperty("谁的QTALK")
    private String whoQtak;

    @ApiModelProperty("通知内容")
    private String content;

    @ApiModelProperty("模板内容")
    private String flowName;

    @ApiModelProperty("通知时间")
    private Date createTime;

    @ApiModelProperty("已读")
    private Boolean read;

    @ApiModelProperty("qtalk消息发送状态(0:消息待发送; 1:消息发送成功; -1:消息发送失败)")
    private int qtalkStatus = 0;

    @ApiModelProperty("mail消息发送状态(0:消息待发送; 1:消息发送成功; -1:消息发送失败)")
    private int mailStatus = 0;

    @ApiModelProperty("流程ID")
    private String flowID;

    @ApiModelProperty("最后操作时间")
    private String updateTime;



    public Notify(String who, String whoQtalk, String content, String flowName, int noticeType, String flowID) {

        this.content = content;
        this.noticeType = noticeType;
        this.flowID = flowID;
        this.who = who;
        this.whoQtak = whoQtalk;
        this.flowName = flowName;
        createTime = new DateTime().toDate();
        updateTime = DateTimeUtil.getDateTime();
        read = false;
    }


    public Notify(Notify notify, String qtalk) {
        this.qtalk = qtalk;
        this.content = notify.getContent();
        this.noticeType = notify.getNoticeType();
        this.flowID = notify.getFlowID();
        this.who = notify.getWho();
        this.whoQtak = notify.getWhoQtak();
        this.flowName = notify.getFlowName();
        createTime = new DateTime().toDate();
        updateTime = DateTimeUtil.getDateTime();
        read = false;
    }

    public Notify(){}
}
