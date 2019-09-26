package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.dao.NotifyRepository;
import com.qunar.superoa.dto.MailInfo;
import com.qunar.superoa.dto.NotifyDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.model.FlowOrder;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.model.OpsappSendMessageInfo;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.NotifyServiceI;
import com.qunar.superoa.service.OpsappApiServiceI;
import com.qunar.superoa.thread.SendNotifyRunable;
import com.qunar.superoa.utils.DateTimeUtil;
import com.qunar.superoa.utils.MailUtil;
import com.qunar.superoa.utils.WebSocketUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_下午9:37
 * @Despriction:
 */

@Service
public class NotifyServiceImpl implements NotifyServiceI {

  private final static Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);

  @Autowired
  private NotifyRepository notifyRepository;

  @Autowired
  private MailUtil mailUtil;

  @Autowired
  private UserService userService;

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  @Autowired
  private FlowServiceImpl flowServiceI;

  @Autowired
  private OpsappApiServiceI opsappApiServiceI;

  @Autowired
  private SendNotifyRunable sendNotifyRunable;

  @Value("${isSendQtalk}")
  private String isSendQtalk;

  @Value("${api.isapi.qunaroa}")
  private String qunaroaApi;

  @Value("${api.isapi.qtalkoa}")
  private String qtalkoaApi;

  @Override
  public void sendNotify(Notify notify) {
    try {
      notify = notifyRepository.save(notify);
      try {
        sendNotifyRunable.startQtalkRunnable(notify);
      } catch (Exception e) {
        logger.error("【ERROR】发送Qtalk通知失败！${}", e);
      }
      try {
        sendNotifyRunable.startMailRunnable(notify);
      } catch (Exception e) {
        logger.error("【ERROR】发送通知邮件失败！${}", e);
      }
      try {
        sendWebNotify(notify);
      } catch (Exception e) {
        logger.error("【ERROR】发送WEB通知失败！${}", e);
      }
    } catch (Exception e) {
      logger.error("【ERROR】保存通知失败！${}", e);
    }
  }

  @Override
  public void sendNotify(Notify notify, String qtalks) {
    Arrays.stream(qtalks.split(",")).forEach(qtalk -> {
      if (qtalk == null || "".equals(qtalk)) {
        return;
      }
      logger.info("创建通知人员：${}", qtalk);
      sendNotify(new Notify(notify, qtalk));
    });
  }

  @Override
  public void readNotify(String ids) {
    notifyRepository.findByIdIn(Arrays.stream(ids.split(",")).filter(x -> x != "" && x != null)
        .collect(Collectors.toList())).forEach(notify -> {
      notify.setRead(true);
      notify.setUpdateTime(DateTimeUtil.getDateTime());
      notifyRepository.save(notify);
    });
  }

  @Override
  public PageResult<NotifyDto> findAll(PageAble pageAble) {
    return new PageResult<>(notifyRepository.findAll(pageAble.getPageAble()), NotifyDto.class);
  }

  @Override
  public PageResult<NotifyDto> findCurrentUserNotify(PageAble pageAble) {
    PageResult<NotifyDto> notifyDtoPageResult = new PageResult(notifyRepository
        .findByQtalk(SecurityUtils.currentUserInfo().getQtalk(), pageAble.getPageAble()),
        NotifyDto.class);
    notifyDtoPageResult.getContent().forEach(notifyDto -> notifyDto
        .setWhoAvatar(userService.findUserAvatarByName(notifyDto.getWhoQtalk())));

    return notifyDtoPageResult;
  }

  @Override
  public PageResult<NotifyDto> findUnread(PageAble pageAble) {
    return new PageResult<>(
        notifyRepository.findAll((Specification<Notify>) (root, query, criteriaBuilder) -> {
          query.where(
              criteriaBuilder.equal(root.get("qtalk"), SecurityUtils.currentUserInfo().getQtalk()),
              criteriaBuilder.equal(root.get("read"), false));
          return null;
        }, pageAble.getPageAble()), NotifyDto.class);
  }


  public boolean sendMail(Notify notify) throws Exception {
    FlowOrder flowOrder = flowOrderRepository.findById(notify.getFlowID()).get();
    String mailContent = flowServiceI.getMailAndQtalkContent(flowOrder).get("mail");
    //发送mail邮件
    MailInfo mailInfo = new MailInfo(flowOrder, "[待办]");
    //发送给发起者本人
    if (notify.getNoticeType() == 1) {
      mailInfo.setPrefix("");
      mailInfo.setSubject(notify.getWho() + notify.getContent() + notify.getFlowName());
      mailInfo.setRelationUserName("您");
      mailInfo.setSuffix("的申请");
    }
    //知会相关人员
    if (notify.getNoticeType() == 2) {
      mailInfo.setPrefix("[知会]");
      mailInfo.setSuffix("的申请");
    }
    //撤销通知待审批人
    if (notify.getNoticeType() == 3) {
      mailInfo.setPrefix("[撤销]");
      mailInfo.setSuffix("的已撤销流程");
      mailInfo.setSubject(notify.getWho() + notify.getContent() + notify.getFlowName());
    }
    //通知其余待审批人邮件已被处理
    if (notify.getNoticeType() == 4) {
      mailInfo.setPrefix("[已处理]");
      mailInfo.setSuffix("的流程已被" + notify.getWho() + "处理");
    }
    mailInfo.setTargetEmail(notify.getQtalk());
    mailInfo.setContent(mailContent);
    mailInfo.setLinkUrl(qunaroaApi + "/cooperate/detail/" + flowOrder.getId());
    //生成邮件中qtalk扫一扫二维码图片
    String qrCodeUrl = flowServiceI.getQRCodeUrl(flowOrder);
    mailInfo.setQrCodeUrl(qrCodeUrl);

    return mailUtil.sendMail(mailInfo);

  }

  public Object sendQtalk(Notify notify) {
    FlowOrder flowOrder = flowOrderRepository.findById(notify.getFlowID()).get();
    String qtalkContent = flowServiceI.getMailAndQtalkContent(flowOrder).get("qtalk");
    //发送qtalk消息
    OpsappSendMessageInfo opsappSendMessageInfo = new OpsappSendMessageInfo(flowOrder, "[待办]");
    //发送给发起者本人
    if (notify.getNoticeType() == 1 || notify.getNoticeType() == 3 || notify.getNoticeType() == 4 ) {
      opsappSendMessageInfo.setTitle(notify.getWho() + notify.getContent() + notify.getFlowName());
    }
    //知会给相关人员
    if (notify.getNoticeType() == 2) {
      opsappSendMessageInfo.setTitle("[知会]" + flowOrder.getHeadline());
    }
    opsappSendMessageInfo.setContent(qtalkContent);
    List<String> qtalkList = Lists.newArrayList();
    qtalkList.add(notify.getQtalk());
    opsappSendMessageInfo.setQtalk_ids(qtalkList);
    opsappSendMessageInfo.setBody(qtalkContent);
    opsappSendMessageInfo.setLinkurl(qunaroaApi + "/cooperate/detail/" + flowOrder.getId());
    opsappSendMessageInfo.setReacturl(qtalkoaApi + "@oid:" + flowOrder.getId());

    //若配置文件不发送qtalk消息，则直接返回发送成功
    if ("false".equalsIgnoreCase(isSendQtalk)) {
      JsonObject qtalkStatus = new JsonObject();
      qtalkStatus.addProperty("errcode", "0");
      return qtalkStatus;
    }
    Object qtalkStatus = opsappApiServiceI.sendQtalkMessage(opsappSendMessageInfo);
    logger.info("发送qtalk消息结果为: {}", qtalkStatus);
    return qtalkStatus;
  }

  private void sendWebNotify(Notify notify) {
    WebSocketUtil.pushMessage(notify);
  }
}
