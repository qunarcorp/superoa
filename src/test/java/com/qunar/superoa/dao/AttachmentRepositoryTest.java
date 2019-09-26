package com.qunar.superoa.dao;

import static org.junit.Assert.*;

import com.qunar.superoa.model.UserAttachment;
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
 * @Date:Created in 下午2:22 2018/11/6
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AttachmentRepositoryTest {

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Test
  public void findFirstByMd5OrderByUploadDateDesc() {
    Optional<UserAttachment> userAttachmentOptional = attachmentRepository.findFirstByMd5OrderByUploadDateDesc("d41d8cd98f00b204e9800998ecf8427e");
    log.info(userAttachmentOptional.get().getUrl());

  }
}