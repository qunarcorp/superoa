package com.qunar.superoa.controller;

import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_上午12:00
 * @Despriction:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest {

  @Autowired
  private MockMvc mvc;

  public String getMessage(String name, int age) {
    Pattern EMAIL_PATTERN = Pattern
        .compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");

    return new StringBuilder().append(name).append(" is ").append(age).append(" years old.")
        .toString();
  }

  @Test
  public void say() {
  }

  @Test
  public void cupSize() {
  }

  @Test
  public void sayParam() {
  }

  @Test
  public void sayParam2() {
  }

  @Test
  public void say2() {
  }

  @Test
  public void girls() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/oa/girls"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void girlAdd() {
  }

  @Test
  public void girlFindOne() {
  }

  @Test
  public void girlByAge() {
  }

  @Test
  public void girl() {
  }

  @Test
  public void girlDelete() {
  }

  @Test
  public void twoGirl() {
  }

  @Test
  public void getAge() {
      coinProblem(100, 29);
  }

  public int coinProblem(int n, int m) {
    // Write your code here
    int balance = n - m;
    int time = 0;
    while (balance == 0) {
      if (balance >= 100) {
        balance = balance - 100;
      } else if (balance >= 50) {
        balance = balance - 50;
      } else if (balance >= 20) {
        balance = balance - 20;
      } else if (balance >= 10) {
        balance = balance - 10;
      } else if (balance >= 5) {
        balance = balance - 5;
      } else if (balance >= 2) {
        balance = balance - 2;
      } else if (balance >= 1) {
        balance = balance - 1;
      }
      time++;
      System.out.println(balance);
    }
    return time;
  }
}