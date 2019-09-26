package com.qunar.superoa.utils;

import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: lee.guo
 * @Despriction: 定时任务日志工具 （同时在控制台和调度中心打印日志）
 * @Date: Created in 11:16 AM 2018/10/30
 * @Modify by: chengyan.liang in 15:13 PM 2018/10/30
 */

@Slf4j
public class TaskLogger {

  public static void info(String msg) {
    log.info(msg);
    XxlJobLogger.log(msg);
  }

  public static void info(String msg, Object object) {
    log.info(msg, object);
    XxlJobLogger.log(msg, object);
  }

  public static void debug(String msg) {
    log.debug(msg);
    XxlJobLogger.log(msg);
  }

  public static void dubug(String msg, Object object) {
    log.debug(msg, object);
    XxlJobLogger.log(msg, object);
  }

  public static void error(String msg) {
    log.error(msg);
    XxlJobLogger.log(msg);
  }

  public static void error(String msg, Object object) {
    log.error(msg, object);
    XxlJobLogger.log(msg, object);
  }
}
