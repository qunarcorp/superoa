package com.qunar.superoa.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
/**
 * @Auther: lee.guo
 * @Date:Created in 2018/10/11_7:04 PM
 * @Despriction: 常用工具
 */

@Slf4j
public class CommonUtil {

  private final static Gson GSON = new GsonBuilder().serializeNulls().create();

  // 一分钟，即60000ms
  private static final long ONE_MINUTE = 60000;

  /**
   * 打印方法执行耗时的信息，如果超过了一定的时间，才打印
   *
   * @param methodName 方法名
   * @param startTime 启动时间
   * @param endTime 结束时间
   */
  public static void printExecTime(String methodName, long startTime, long endTime) {
    long diffTime = endTime - startTime;
    log.info("│ 方法执行耗时： {}ms   {}", diffTime, methodName);
    if (diffTime > ONE_MINUTE) {
      log.warn("-----" + methodName + " 方法执行耗时：" + diffTime + " ms");
    }
  }

  /**
   * string to map
   *
   * @param object 结果对象
   * @return Map<String   ,       Object>
   */
  public static Map<String, Object> s2m(Object object) {
    return GSON.fromJson(object.toString(), new TypeToken<Map<String, Object>>() {
    }.getType());
  }

  /**
   * object to map
   *
   * @param obj 结果对象
   * @return Map<String   ,       Object>
   */
  public static Map<String, Object> o2m(Object obj) {
    try {
      Object str = GSON.toJson(obj);
      return GSON.fromJson(str.toString(), new TypeToken<Map<String, Object>>() {
      }.getType());
    } catch (Exception e){
      return s2m(obj);
    }
  }

  /**
   * 获取obj中的data对象并返回data的map
   *
   * @param obj 结果对象
   * @return Map<String   ,       Object>
   */
  public static Map<String, Object> getMapData(Object obj) {
    try {
      return o2m(o2m(obj).get("data"));
    } catch (Exception ex) {
      return o2m(s2m(obj).get("data"));
    }
  }

  /**
   * 获取obj中的data对象并返回data的map
   *
   * @param obj 结果对象
   * @return ArrayList<Map   <   String   ,       String>>
   */
  public static ArrayList<Map<String, String>> getListData(Object obj) {
    return o2A(o2m(obj).get("data"));
  }

  /**
   * 获取obj中的data对象并返回data的object
   *
   * @param object 结果对象
   * @return Object
   */
  public static Object getObjectData(Object object) {
    return o2m(object).get("data");
  }

  /**
   * object to map
   *
   * @param obj 结果对象
   * @return ArrayList<Map<String, String>>
   */
  public static ArrayList<Map<String, String>> o2A(Object obj) {
    return GSON
        .fromJson(GSON.toJson(obj), new TypeToken<ArrayList<Map<String, String>>>() {
        }.getType());
  }

}
