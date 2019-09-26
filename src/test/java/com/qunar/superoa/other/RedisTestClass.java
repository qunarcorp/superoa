package com.qunar.superoa.other;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/25_下午3:05
 * @Despriction: 测试redis
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTestClass {

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Test
  public void test() {
    stringRedisTemplate.opsForValue().set("aaa", "111");
    Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
  }
}
