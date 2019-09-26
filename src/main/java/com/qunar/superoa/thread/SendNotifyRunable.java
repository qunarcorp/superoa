package com.qunar.superoa.thread;

import com.qunar.superoa.dao.NotifyRepository;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.service.ipml.NotifyServiceImpl;
import com.qunar.superoa.utils.CommonUtil;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 22:37 2018/11/11
 * @Modify by:
 */

@Slf4j
@Component
public class SendNotifyRunable {

  @Autowired
  private NotifyServiceImpl notifyService;

  @Autowired
  private NotifyRepository notifyRepository;

  private ExecutorService pool = ThreadPool.createThreadPool();

  public SendNotifyRunable() {
  }

  /**
   * 启动发送qtalk线程
   */
  public void startQtalkRunnable(Notify notify) {
    pool.execute(new SendQtalk(notify));
  }

  /**
   * 启动发送mail线程
   */
  public void startMailRunnable(Notify notify) {
    pool.execute(new SendMail(notify));
  }

  /**
   * 发送Qtalk线程类
   */
  private class SendQtalk implements Runnable {

    private Notify notify;

    public SendQtalk(Notify notify) {
      this.notify = notify;
    }

    @Override
    public void run() {
      log.info("begin sendQtalk");
      int flag = 1;
      while (flag < 4) {
        log.info("sendQtalk 第{}次 send to {}", flag, notify.getQtalk());
        try {
          Map<String, Object> map = CommonUtil.o2m(SendNotifyRunable.this.notifyService.sendQtalk(notify));
          if ("0.0".equals(String.valueOf(map.get("errcode"))) ||
              "0".equals(String.valueOf(map.get("errcode")))) {
            notify.setQtalkStatus(1);
            break;
          } else {
            notify.setQtalkStatus(-1);
            flag++;
          }
        } catch (NoSuchElementException e) {
          log.info("sendQtalk nosuchelementException,重试");
          e.printStackTrace();
          flag = 1;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          continue;
        } catch (Exception e) {
          e.printStackTrace();
          notify.setQtalkStatus(-1);
          flag++;
          continue;
        }
      }
      SendNotifyRunable.this.notifyRepository.save(notify);
      log.info("sendQtalk to {} status:{}", notify.getQtalk(), notify.getQtalkStatus());
    }
  }

  /**
   * 发送Mail线程类
   */
  private class SendMail implements Runnable {

    private Notify notify;

    public SendMail(Notify notify) {
      this.notify = notify;
    }

    @Override
    public void run() {
      log.info("begin sendMail");
      int flag = 1;
      while (flag < 4) {
        log.info("sendMail 第{}次 send to {}", flag, notify.getWhoQtak());
        try {
          if (SendNotifyRunable.this.notifyService.sendMail(notify)) {
            notify.setMailStatus(1);
            break;
          } else {
            notify.setMailStatus(-1);
            flag++;
          }
        } catch (NoSuchElementException e) {
          log.info("sendMail nosuchelementException,重试");
          e.printStackTrace();
          flag = 1;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          continue;
        } catch (Exception e) {
          e.printStackTrace();
          notify.setQtalkStatus(-1);
          flag++;
          continue;
        }
      }
      SendNotifyRunable.this.notifyRepository.save(notify);
      log.info("sendMail to {} status:{}", notify.getQtalk(), notify.getMailStatus());
    }

  }


}
