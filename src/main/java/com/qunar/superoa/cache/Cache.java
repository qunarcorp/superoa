package com.qunar.superoa.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/27_8:01 PM
 * @Despriction: cache
 */

public class Cache {


  private static Map<String, Object> flowDataCache = new HashMap();

  public static Map<String, Object> getAllFlowDataCache() {
    return flowDataCache;
  }

  public static Object getFlowDataCacheByKey(String key) {
    return flowDataCache.get(key);
  }

  public static void setFlowDataCache(String key, Object object) {
    flowDataCache.put(key, object);
  }

  public static void deleteFlowDataCache(String key) {
    flowDataCache.remove(key);
  }

}
