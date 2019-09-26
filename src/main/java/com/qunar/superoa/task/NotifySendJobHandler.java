package com.qunar.superoa.task;

import com.qunar.superoa.dao.NotifyRepository;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.service.ipml.NotifyServiceImpl;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.TaskLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 上午11:55 2018/11/9
 * @Modify by:
 */
/**
 * 定时监测消息发送失败通知并重发
 */
@JobHandler(value = "notifySendJobHandler")
@Component
public class NotifySendJobHandler extends IJobHandler {

  @Autowired
  private NotifyRepository notifyRepository;

  @Autowired
  private NotifyServiceImpl notifyService;


  @Override
  public ReturnT<String> execute(String s) throws Exception {

    TaskLogger.info("定时监测发送失败的qtalk消息，任务开始");
    //获取未成功发送qtalk消息的notify列表
    List<Notify> qtalkNotifyList = notifyRepository.findAllByQtalkStatus(-1);
    //数据库中发送qtalk消息失败条数
    AtomicInteger failQtalkNotifyCount = new AtomicInteger(qtalkNotifyList.size());
    //重发后qtalk消息发送成功条数
    AtomicInteger successQtalkSendCount = new AtomicInteger(0);
    if (qtalkNotifyList.size() > 0) {
      qtalkNotifyList.forEach(notify -> {
        Map<String,Object> map = CommonUtil.o2m(notifyService.sendQtalk(notify));
        if ("0.0".equals(String.valueOf(map.get("errcode"))) ||
            "0".equals(String.valueOf(map.get("errcode")))) {
          notify.setQtalkStatus(1);
          notifyRepository.save(notify);
          successQtalkSendCount.getAndIncrement();
        }
      });
    }
    TaskLogger.info("数据库中发送qtalk消息失败条数: {}",failQtalkNotifyCount);
    TaskLogger.info("重发后qtalk消息发送成功条数: {}",successQtalkSendCount);
    TaskLogger.info("定时监测发送失败的qtalk消息，任务结束");
    return SUCCESS;
  }
}
