package com.qunar.superoa.service;

import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.dao.DepartmentRepository;
import com.qunar.superoa.model.UserAttachment;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午3:51 2018/10/29
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class DepartmentServiceTest {

  @Autowired
  AttachmentRepository attachmentRepository;

  @Test
  public void attachmentTest() {
    Optional<List<UserAttachment>> userAttachmentList = attachmentRepository.findAllByExpiredDateBefore(new Date());
    log.info(userAttachmentList.toString());
  }

  @Test
  public void swiftDeleteTest() {
  }

  @Test
  public void urlExistTest() {
    boolean isAlive = true;
    try {
      URL url = new URL("https://blog.csdn.net/fangqun663775/article/details/45309287");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(1000*30);
      connection.connect();
      int code = connection.getResponseCode();
      boolean success = (code >= 200) && (code < 300) ;
      if (!success) {
        isAlive = false;
      }
      connection.disconnect();
    } catch (Exception e) {
      isAlive = false;
    }
    log.info(String.valueOf(isAlive));
  }

}
