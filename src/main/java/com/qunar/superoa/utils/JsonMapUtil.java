package com.qunar.superoa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: xing.zhou
 * @Despriction:
 * @Date:Created in 11:36 2018/10/17
 * @Modify by:
 */
@Component
public class JsonMapUtil {

  @Autowired
  private UserInfoUtil userInfoUtil;

  private final static String UNDER_LINE = "_";
  private final static String KEY = "k";
  private final static String VAL = "v";
  private final static String LIST = "list";
  private final static String LIST_COMPARE = "listCompare";
  private final static String DEPART = "department_";
  private final static String EQ = "=";
  private final static String NEQ = "!=";
  private final static String GT = ">";
  private final static String LT = "<";
  private final static String GE = ">=";
  private final static String LE = "<=";

  /**
   * 通过key map 获取值  key为k类型
   */
  public static String getValueByKey(String key, Map<String, Object> map) {
    StringBuffer result = new StringBuffer();
    for (String k : map.keySet()) {
      if (k.equals(key)) {
        result.append(map.get(key)).append(",");
      } else if (map.get(k) instanceof Map) {
        String temp = getValueByKey(key, (Map<String, Object>) map.get(k));
        if (StringUtils.isNotBlank(temp)) {
          result.append(temp).append(",");
        }
      } else if (map.get(k) instanceof List) {
        if (((List) map.get(k)).size() > 0 && ((List) map.get(k)).get(0) instanceof Map) {
          for (Map mapChild : (List<Map>) map.get(k)) {
            String temp = getValueByKey(key, mapChild);
            if (StringUtils.isNotBlank(temp)) {
              result.append(temp).append(",");
            }
          }
        } else {
          continue;
        }
      } else {
        continue;
      }
    }
    return StringUtils.isNotBlank(result.toString()) ? result.deleteCharAt(result.length() - 1).toString()
        : result.toString();
  }

  /**
   * 通过key map 获取值  key为v类型
   */
  public static String getValueByValue(String value, String map) {
    String[] values = map.replaceAll("\\[", "").replaceAll("]", "").split(",");
    for (String v : values) {
      if (v.trim().equals(value)) {
        return "true";
      }
    }
    return "false";
  }


  /**
   * 通过key map 获取值  key为list类型
   */
  public static String getValueByList(String value, String key, Map<String, Object> map) {
    String[] keys = key.split(",");
    List<String> list = new ArrayList<>();
    for (String k : keys) {
      list.add(getValueByKey(k, map));
    }
    String[] values = value.split(",");
    boolean result = false;
    for (int i = 0; i < list.get(0).split(",").length; i++) {
      for (int j = 0; j < values.length; j++) {
        String str = list.get(j);
        if ("!".equals(values[j].substring(0, 1))) {
          if (values[j].substring(1, values[j].length() - 1).equals(str.split(",")[i])) {
            result = false;
            break;
          } else {
            result = true;
          }
        } else {
          if (!values[j].equals(str.split(",")[i])) {
            result = false;
            break;
          } else {
            result = true;
          }
        }
      }
      if (result) {
        return "true";
      }
    }
    return "false";
  }

  /**
   * 通过key map 获取值  key为listCompare 类型
   */
  public static String getValueByListCompare(String key, String value, String type, Map<String, Object> map){
    String[] keys = key.split(",");
    String[] values = value.split(",");
    String[] types = type.split(",");
    //获取每个key对应的values
    List<String> vList = new ArrayList<>();
    for (String k : keys) {
      vList.add(getValueByKey(k, map));
    }
    boolean result = false;
    //循环每行数据
    for (int i = 0; i < vList.get(0).split(",").length; i++) {
      //循环每列数据
      for (int j = 0; j < values.length; j++) {
        String vForms = vList.get(j);
        double vForm = Double.parseDouble(vForms.split(",")[i]);
        double vStandard = Double.parseDouble(values[j]);
        switch (types[j]) {
          case EQ:
            if(vForm == vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          case NEQ:
            if(vForm != vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          case GT:
            if(vForm > vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          case LT:
            if(vForm < vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          case GE:
            if(vForm >= vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          case LE:
            if(vForm <= vStandard){
              result = true;
            }else{
              result = false;
            }
            break;
          default:
            break;
        }
        if(!result){
          break;
        }
      }
      if (result) {
        return "true";
      }
    }
    return "false";
  }

  /**
   * 通过key map 获取值
   */
  public String getValueByMap(String key, Map<String, Object> map, String currentUserId) {
    if (key.contains(DEPART)) {
      //部门单独处理
      return getDepartment(key, currentUserId);
    }
    String[] keys = key.split(UNDER_LINE);
    if (KEY.equals(keys[0])) {
      //k类型key  取值
      return getValueByKey(keys[1], map);
    } else if (VAL.equals(keys[0])) {
      //v类型key  取值
      return getValueByValue(keys[2], getValueByKey(keys[1], map));
    } else if (LIST.equals(keys[0])) {
      //list类型key  取值
      return getValueByList(keys[2], keys[1], map);
    } else if (LIST_COMPARE.equals(keys[0])){
      //listCompare类型key  取值
      return getValueByListCompare(keys[1], keys[2], keys[3], map);
    }
    return "";
  }

  public String getDepartment(String key, String currentUserId) {
    return userInfoUtil.getUserDeptByLevel(currentUserId,
        Integer.parseInt(key.split(UNDER_LINE)[1]));
  }

  public static List<String> getKeysByKey(Map<String, Object> map) {
    List<String> result = new ArrayList<>();
    if (map.get("isSummary") != null && "true".equals(map.get("isSummary").toString())) {
      result.add(map.get("title").toString());
    }
    for (String k : map.keySet()) {
      if (map.get(k) instanceof Map) {
        result.addAll(getKeysByKey((Map) map.get(k)));
      } else if (map.get(k) instanceof List) {
        if (((List) map.get(k)).size() > 0 && ((List) map.get(k)).get(0) instanceof Map) {
          for (Map mapChild : (List<Map>) map.get(k)) {
            result.addAll(getKeysByKey(mapChild));
          }
        }
        continue;
      } else {
        continue;
      }
    }
    return result;
  }

  /**
   * 获取所有key
   */
  public static Map<String, String> getAllKeys(Map<String, Object> map) {
    Map<String, String> resultMap = new HashMap<>();
    map.keySet().forEach(k -> {
      if (!"summary".equalsIgnoreCase(k)) {
        if (map.get(k) instanceof Map) {
          resultMap.putAll(getAllKeys((Map) map.get(k)));
        } else if (map.get(k) instanceof List) {
          if (((List) map.get(k)).size() > 0 && ((List) map.get(k)).get(0) instanceof Map) {
            ((List<Map>) map.get(k)).forEach(mapChild -> resultMap.putAll(getAllKeys(mapChild)));
          } else {
            if (((List) map.get(k)).size() == 0) {
              resultMap.put(k, "");
            } else {
              StringBuffer value = new StringBuffer();
              ((List) map.get(k)).forEach(v -> value.append(v).append(","));
              resultMap.put(k, value.toString());
            }
          }
        } else {
          resultMap.put(k, map.get(k).toString());
        }
      }
    });
    return resultMap;
  }
}
