package com.qunar.superoa.utils;

import static org.junit.Assert.*;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 上午11:04 2018/11/6
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FileMD5UtilsTest {

  @Autowired
  private FileMD5Utils fileMD5Utils;

  @Test
  public void getMd5ByFile() {
    //String md5 = fileMD5Utils.getMd5ByFile(new File("/Users/qitmac000560/Desktop/group.zip"));
    String md5 = fileMD5Utils.getMd5ByFile(new File("/Users/qitmac000560/Downloads/g.zip"));
    log.info(md5);
  }

  @Test
  public void getMd5ByInputStream() {
  }

  @Test
  public void getMd5ByMultiFile() {
  }
}