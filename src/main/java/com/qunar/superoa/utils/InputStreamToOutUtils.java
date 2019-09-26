package com.qunar.superoa.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午3:01 2018/11/6
 * @Modify by:
 */

/**
 * 流的转换
 */
@Component
public class InputStreamToOutUtils {

  public ByteArrayOutputStream inputStreamToOut(InputStream input) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len;
    while ((len = input.read(buffer)) > -1 ) {
      baos.write(buffer, 0, len);
    }
    baos.flush();

    return baos;
  }

}
