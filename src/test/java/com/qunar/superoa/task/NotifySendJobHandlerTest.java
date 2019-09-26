package com.qunar.superoa.task;

import static org.junit.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午3:17 2018/11/9
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NotifySendJobHandlerTest {

  @Autowired
  private NotifySendJobHandler notifySendJobHandler;

  @Test
  public void notifySendTest() throws Exception {
    //notifySendJobHandler.execute("s");
  }

}