package com.qunar.superoa.other;

import com.qunar.superoa.security.SecurityUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/25_下午5:34
 * @Despriction: 测试登录相关
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityTest {

  @Autowired
  private SessionRegistry sessionRegistry;

  @Autowired
  private SecurityUtils securityUtils;

  @Test
  public void test() {
    log.info("u begin");
    securityUtils.logInAs("lee.guo");
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    log.info("${}", allPrincipals.size());
    allPrincipals.forEach(System.out::println);
    //可以转换成spring的User}
    log.info("u end");
  }
}
