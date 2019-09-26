package com.qunar.superoa.dto;

import com.qunar.superoa.model.Notify;
import com.qunar.superoa.utils.DateTimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/12_下午1:39
 * @Despriction:
 */

@Data
public class NotifyDto {
    @ApiModelProperty("通知ID")
    private String id;

    @ApiModelProperty("通知人")
    private String qtalk;

    @ApiModelProperty("通知类型(0:点击跳转待办；  1:点击跳转已申请; -1: 不跳转任何页面)")
    private int noticeType;

    @ApiModelProperty("谁")
    private String who;

    @ApiModelProperty("谁的QTALK")
    private String whoQtalk;

    @ApiModelProperty("谁的头像")
    private String whoAvatar;

    @ApiModelProperty("通知内容")
    private String content;

    @ApiModelProperty("模板名称")
    private String flowName;

    @ApiModelProperty("通知时间")
    private String createTime;

    @ApiModelProperty("已读")
    private Boolean read;

    @ApiModelProperty("流程ID")
    private String flowID;

    @ApiModelProperty("最后操作时间")
    private String updateTime;

    public NotifyDto(Object model){
        Notify notify = (Notify)model;
        this.id = notify.getId();
        this.qtalk = notify.getQtalk();
        this.noticeType = notify.getNoticeType();
        this.content = notify.getContent();
        this.createTime = DateTimeUtil.toString(notify.getCreateTime());
        this.read = notify.getRead();
        this.flowID = notify.getFlowID();
        this.updateTime = notify.getUpdateTime();
        this.who = notify.getWho();
        this.whoQtalk = notify.getWhoQtak();
        this.flowName = notify.getFlowName();
    }
}
