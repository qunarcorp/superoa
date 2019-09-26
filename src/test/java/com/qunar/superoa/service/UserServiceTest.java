package com.qunar.superoa.service;

import com.qunar.superoa.service.ipml.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午2:59 2018/9/29
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @Test
  public void userUrlTest() {
    String url = userService.findUserAvatarByName("mingliang.gao") ;
    System.out.println(url);
  }

}
