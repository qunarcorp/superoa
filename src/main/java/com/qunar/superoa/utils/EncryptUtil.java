package com.qunar.superoa.utils;

import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.EncryptException;
import java.security.MessageDigest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction: 加密解密算法
 * @Date:Created in 3:01 PM 2019/3/19
 * @Modify by:
 */
@Slf4j
@Component
public class EncryptUtil {

  private String token = "*************";

  private static final Long EXPIRE_TIME = 180L;

  public boolean checkSignature(String signature, String timeStamp, String userName) {
    //时间戳、用户名是否为空
    if (StringUtils.isBlank(timeStamp) || StringUtils.isBlank(userName)) {
      return false;
    }
    long time;
    try {
      time = Long.parseLong(timeStamp);
    } catch (NumberFormatException e) {
      throw new EncryptException(ResultEnum.ENCRYPT_TIMESTAMP_ERROR);
    }
    //是否过期
    if (isTimeExpired(time)) {
      return false;
    }
    String cipherText = encrypt(timeStamp, userName);
    return signature.equalsIgnoreCase(cipherText);
  }

  /**
   * 加密算法
   */
  public String encrypt(String timeStamp, String userName) {
    //对Token、timeStamp按字典排序
    String[] paramArr = new String[] {token, timeStamp, userName};
    Arrays.sort(paramArr);

    //将排序后的结果拼成一个字符串
    String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);

    String cipherText;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      byte[] digest = messageDigest.digest(content.getBytes());
      cipherText = byteToStr(digest);
      return cipherText;
    } catch (Exception e) {
      log.error("加密信息失败", e);
      return "";
    }
  }

  /**
   * 验证签名是否过期
   */
  private boolean isTimeExpired(Long timeStamp) {
    //过期时间参数为0，则永不过期
    if (EXPIRE_TIME == 0L) {
      return false;
    }

    return System.currentTimeMillis() - timeStamp > EXPIRE_TIME * 1000;

  }

  /**
   * 将字节数组转换为十六进制字符串
   */
  private String byteToStr(byte[] byteArray) {
    StringBuilder strDigest = new StringBuilder();
    for (byte aByteArray : byteArray) {
      strDigest.append(byteToHexStr(aByteArray));
    }
    return strDigest.toString();
  }

  /**
   * 将字节转换为十六进制字符串
   */
  private String byteToHexStr(byte mByte) {
    char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    char[] tempArr = new char[2];
    tempArr[0] = digit[(mByte >>> 4) & 0X0F];
    tempArr[1] = digit[mByte & 0X0F];

    return new String(tempArr);
  }
}
