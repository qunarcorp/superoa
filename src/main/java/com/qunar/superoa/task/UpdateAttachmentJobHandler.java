package com.qunar.superoa.task;

import com.google.common.collect.Lists;
import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.utils.MinioUtils;
import com.qunar.superoa.utils.TaskLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午9:09 2018/10/29
 * @Modify by:
 */
@Component
@JobHandler(value = "updateAttachmentJobHandler")
public class UpdateAttachmentJobHandler extends IJobHandler {

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private MinioUtils minioUtils;

  @Override
  @Transactional
  public ReturnT<String> execute(String s) throws Exception {

    TaskLogger.info("更新并删除user_attachment附件任务开始：");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    //过期文件
    Optional<List<UserAttachment>> userAttachmentList = attachmentRepository
        .findAllByExpiredDateBefore(new Date());
    List<UserAttachment> delUserAttachmentList = Lists.newArrayList();
    List<String> unDeletedFiles = Lists.newArrayList();
    //遍历userAttachments并进相关处理
    userAttachmentList.ifPresent(userAttachments -> userAttachments.forEach(userAttachment -> {
      boolean delFile = false;
      try {
        delFile = minioUtils.deleteFileFromMinio(userAttachment.getAttachName());
      } catch (Exception e) {
        TaskLogger.error("从minio删除文件失败", e);
        TaskLogger.error("删除失败文件信息:{}", userAttachment);
      }
      if(delFile) {
        delUserAttachmentList.add(userAttachment);
      } else {
        unDeletedFiles.add(userAttachment.getAttachName());
      }
    }));
    //将从minio成功删除的文件对应的attachment也对应删除
    attachmentRepository.deleteAll(delUserAttachmentList);
    long time = stopWatch.getTime();
    stopWatch.stop();
    TaskLogger.info("从minio被删除的文件如下 :");
    if (delUserAttachmentList.size() > 0 ) {
      delUserAttachmentList.forEach(delUserAttachment -> TaskLogger.info(delUserAttachment.getAttachName()));
    }
    TaskLogger.info("从数据库被删除文件数量 : {}", delUserAttachmentList.size());
    TaskLogger.info("过期未被minio服务器删除的文件如下 ：");
    if (unDeletedFiles.size() > 0 ) {
      unDeletedFiles.forEach(TaskLogger::info);
    }
    TaskLogger.info("过期未被minio服务器删除的文件数量 : {}", unDeletedFiles.size());
    TaskLogger.info("更新并删除user_attachment附件任务结束，用时 {}", time);

    return SUCCESS;
  }
}
