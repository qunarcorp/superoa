package com.qunar.superoa.utils;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午2:31 2018/10/30
 * @Modify by:
 */

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 * 检验url地址是否连通
 */
@Component
public class UrlAliveUtil {

  public boolean isUrlAlive(String url) {
    boolean isAlive = true;
    HttpURLConnection connection;

    try {
      URL newUrl = new URL(url);
      connection = (HttpURLConnection) newUrl.openConnection();
      connection.setConnectTimeout(1000 * 30);
      connection.connect();
      int code = connection.getResponseCode();
      boolean success = (code >= 200) && (code < 300);
      if (!success) {
        isAlive = false;
      }
      connection.disconnect();
    } catch (Exception e) {
      isAlive = false;
    }
    return isAlive;
  }

}
