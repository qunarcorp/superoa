package com.qunar.superoa.thread;

import com.qunar.superoa.dto.MailInfo;
import com.qunar.superoa.model.OpsappSendMessageInfo;
import com.qunar.superoa.service.OpsappApiServiceI;
import com.qunar.superoa.utils.MailUtil;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction: 催办发送通知多线程
 * @Date:Created in 9:25 PM 2019/4/15
 * @Modify by:
 */
@Slf4j
@Component
public class RemindApproveUsersRunnable {

  @Autowired
  private MailUtil mailUtil;

  @Autowired
  private OpsappApiServiceI opsappApiServiceI;

  private ExecutorService pool = ThreadPool.createThreadPool();

  /**
   * 启动发送qtalk线程
   */
  public void startQtalkRemindRunnable(OpsappSendMessageInfo opsappSendMessageInfo) {
    pool.execute(new SendQtalk(opsappSendMessageInfo));
  }

  /**
   * 启动发送mail线程
   */
  public void startMailRemindRunnable(MailInfo mailInfo) {
    pool.execute(new SendMail(mailInfo));
  }

  /**
   * 发送qtalk线程类
   */
  private class SendQtalk implements Runnable {

    private OpsappSendMessageInfo opsappSendMessageInfo;

    public SendQtalk(OpsappSendMessageInfo opsappSendMessageInfo) {
      this.opsappSendMessageInfo = opsappSendMessageInfo;
    }

    @Override
    public void run() {
      log.info("催办发送qtalk通知开始, 单号URL: {}", opsappSendMessageInfo.getLinkurl());
      Object remindQtalkStatus = "未返回结果";
      try {
        remindQtalkStatus = opsappApiServiceI.sendQtalkMessage(opsappSendMessageInfo);
      } catch (Exception e) {
        log.error("发送qtalk催办通知失败", e);
      }
      log.info("催办发送qtalk结果: {}", remindQtalkStatus);
    }
  }

  /**
   * 发送mail线程类
   */
  private class SendMail implements Runnable {

    private MailInfo mailInfo;

    public SendMail(MailInfo mailInfo) {
      this.mailInfo = mailInfo;
    }

    @Override
    public void run() {
      log.info("催办发送mail通知开始, 单号URL: {}", mailInfo.getLinkUrl());
      boolean remindMailStatus = false;
      try {
        remindMailStatus = mailUtil.sendMail(mailInfo);
      } catch (Exception e) {
        log.error("发送催办mail邮件失败", e);
      }
      log.info("催办发送mail结果: {}", remindMailStatus ? "成功" : "失败");
    }
  }
}
