package com.qunar.superoa.service.ipml;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/27_7:33 PM
 * @Despriction: ldap测试
 */


@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapServiceTest {

  @Autowired
  private LdapService ldapService;

  @Test
  public void authenticate() {
    Assert.isTrue(ldapService.authenticate("lee.guo", "Qunar.9333"), "执行失败");
  }
}