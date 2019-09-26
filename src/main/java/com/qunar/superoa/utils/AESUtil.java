package com.qunar.superoa.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64.Decoder;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;

/**
 * @Auther: chengyan.liang
 * @Despriction: AES加密解密算法
 * @Date:Created in 10:16 AM 2019/4/9
 * @Modify by:
 */
public class AESUtil {
  /**
   * 加密解密算法
   */
  private static final String ALGORITHM = "AES";
  /**
   * 加解密算法/工作模式/填充方式
   */
  private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
  /**
   * 生成key
   */
  private static SecretKeySpec key = new SecretKeySpec("TbyacXAvOYPPI94i".getBytes(), ALGORITHM);

  static SecretKey secretKey;

  /**
   * AES加密
   * @param data 待加密数据
   * @return 加密后的数据
   * @throws Exception
   */
  public static String encryptData(String data) throws Exception {
    // 创建密码器
    Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
    //初始化，加密模式
    cipher.init(Cipher.ENCRYPT_MODE, key);
    //生成密码
    return new String(Base64.encode(cipher.doFinal(data.getBytes())));
  }

  /**
   * AES解密
   * @param data 待解密的数据
   * @param keyMd5 解密的key
   * @return 解密后的数据
   * @throws Exception
   */
  public static String decryptData(String data, String keyMd5) throws Exception {
    // 创建密码器
    Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
    //解密密钥
    secretKey = new SecretKeySpec(keyMd5.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    //初始化，解密模式
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    Decoder decoder = java.util.Base64.getDecoder();
    byte[] decrypt = cipher.doFinal(decoder.decode(data));

    return new String(decrypt, StandardCharsets.UTF_8);
  }
}
