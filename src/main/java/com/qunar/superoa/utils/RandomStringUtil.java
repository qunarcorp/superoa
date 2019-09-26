package com.qunar.superoa.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @Name RandomStringUtil
 * @Descr 生成随机字符串
 */
public class RandomStringUtil {

  /**
   * @param passLength : 要生成多少长度的字符串
   * @param type : 需要哪种类型
   * type=0：纯数字(0-9)
   * type=1：全小写字母(a-z)
   * type=2：全大写字母(A-Z)
   * type=3: 数字+小写字母
   * type=4: 数字+大写字母
   * type=5：大写字母+小写字母
   * type=6：数字+大写字母+小写字母
   * type=7：固定长度33位：根据UUID拿到的随机字符串，去掉了四个"-"(相当于长度33位的小写字母加数字)
   */
  public static String getRandomCode(int passLength, int type) {
    StringBuffer buffer = null;
    StringBuilder sb = new StringBuilder();
    Random r = new Random();
    r.setSeed(System.currentTimeMillis());
    switch (type) {
      case 0:
        buffer = new StringBuffer("0123456789");
        break;
      case 1:
        buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
        break;
      case 2:
        buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        break;
      case 3:
        buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
        break;
      case 4:
        buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        break;
      case 5:
        buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        break;
      case 6:
        buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
        passLength -= 1;
        break;
      case 7:
        String s = UUID.randomUUID().toString();
        sb.append(s, 0, 8).append(s, 9, 13).append(s, 14, 18)
            .append(s, 19, 23).append(s.substring(24));
        break;
      default:
        break;
    }

    if (type != 7) {
      assert buffer != null;
      int range = buffer.length();
      for (int i = 0; i < passLength; ++i) {
        sb.append(buffer.charAt(r.nextInt(range)));
      }
    }
    return sb.toString();
  }
}