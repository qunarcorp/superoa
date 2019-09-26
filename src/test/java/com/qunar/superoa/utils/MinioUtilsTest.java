package com.qunar.superoa.utils;

import com.google.common.collect.Lists;
import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.model.UserAttachment;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:26 PM 2019/4/24
 * @Modify by:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MinioUtilsTest {

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private MinioUtils minioUtils;

  @Test
  public void deleteAttachmentTest() {
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
        log.error("从minio删除文件失败", e);
        log.error("删除失败文件信息:{}", userAttachment);
      }
      if(delFile) {
        delUserAttachmentList.add(userAttachment);
      } else {
        unDeletedFiles.add(userAttachment.getAttachName());
      }
    }));
    //将从minio成功删除的文件对应的attachment也对应删除
    attachmentRepository.deleteAll(delUserAttachmentList);
    log.info("从数据库被删除的文件如下 :");
    if (delUserAttachmentList.size() > 0 ) {
      delUserAttachmentList.forEach(delUserAttachment -> TaskLogger.info(delUserAttachment.getAttachName()));
    }
    log.info("从数据库被删除文件数量 : {}", delUserAttachmentList.size());
    log.info("过期未被ceph服务器删除的文件如下 ：");
    if (unDeletedFiles.size() > 0 ) {
      unDeletedFiles.forEach(TaskLogger::info);
    }
    log.info("过期未被ceph服务器删除的文件数量 : {}", unDeletedFiles.size());
  }

}