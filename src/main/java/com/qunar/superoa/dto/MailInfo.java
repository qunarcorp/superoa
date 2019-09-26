package com.qunar.superoa.dto;


import com.qunar.superoa.model.Notify;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MailInfo {

    @ApiModelProperty("主题")
    private String subject;
    @ApiModelProperty("关系人姓名")
    private String relationUserName;
    @ApiModelProperty("内容")
    private String content;
    @ApiModelProperty("抄送")
    private String ccListMail;
    @ApiModelProperty("邮件组")
    private String emailGroup;
    @ApiModelProperty("收件人")
    private String targetEmail;
    @ApiModelProperty("链接")
    private String linkUrl;
    @ApiModelProperty("标题前缀")
    private String prefix;
    @ApiModelProperty("标题后缀")
    private String suffix;
    @ApiModelProperty("qtalk审批扫一扫的图片地址")
    private String qrCodeUrl;

    public MailInfo(Notify notify) {
        this.subject = "Qunar OA 通知";
        this.content = new StringBuilder().append(notify.getWho()).append(" ").append(notify.getContent()).append(" ").append(notify.getFlowName()).toString();
        this.targetEmail = notify.getQtalk();
    }

    public MailInfo(com.qunar.superoa.model.FlowOrder flowOrder,String prefix) {

        this.subject = flowOrder.getHeadline();
        this.targetEmail = flowOrder.getNextApproveUsers();
        this.relationUserName = flowOrder.getApplyUserName();
        this.prefix = prefix;
        this.suffix = "的申请待您审批";

    }
}
