package com.qunar.superoa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 上午10:27 2018/11/6
 * @Modify by:
 */

/**
 * 获取文件md5值
 */
@Component
@Slf4j
public class FileMD5Utils {

  /**
   * 根据文件获取md5值
   */
  public String getMd5ByFile(File file) {

    String md5 = null;

    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));

    } catch (FileNotFoundException e) {
      log.error("获取文件inputStream失败",e);
    } catch (IOException e) {
      log.error("根据inputStream获取md5值失败",e);
    }
    return md5;
  }

  /**
   *根据InputStream获取md5值
   */
  public String getMd5ByInputStream(InputStream inputStream) {
    String md5 = null;

    try {
      md5 = DigestUtils.md5Hex(IOUtils.toByteArray(inputStream));
    } catch (IOException e) {
      log.error("根据inputStream获取文件md5值失败",e);
    }
    return md5;
  }

  /**
   *根据MultiPartFile获取md5值
   */
  public String getMd5ByFile(MultipartFile multipartFile) {
    String md5 = null;

    try {
      InputStream inputStream = multipartFile.getInputStream();
      md5 = DigestUtils.md5Hex(IOUtils.toByteArray(inputStream));
    } catch (IOException e) {
      log.error("获取multiPartFile文件的md5值失败",e);
    }
    return md5;
  }

}
