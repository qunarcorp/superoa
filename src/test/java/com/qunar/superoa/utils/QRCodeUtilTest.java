package com.qunar.superoa.utils;

import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.model.UserAttachment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午5:53 2018/11/9
 * @Modify by:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class QRCodeUtilTest {

  @Autowired
  private MinioUtils minioUtils;

}