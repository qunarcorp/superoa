package com.qunar.superoa.utils;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午7:40 2018/10/29
 * @Modify by:
 */

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

/**
 * json工具类
 */
@Component
public class JsonUtil {

  /**
   * 开启驼峰式和下划线式自动转换
   */
  private static GsonBuilder gsonBuilder = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls();


  public Gson getGson() {
    return gsonBuilder.create();
  }

}
