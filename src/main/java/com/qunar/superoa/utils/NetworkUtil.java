package com.qunar.superoa.utils;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkUtil {

  /**
   * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
   *
   * @param request 参数
   * @return 返回IP地址
   */
  public static String getIpAddress(HttpServletRequest request) {
    // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

    String ip = request.getHeader("X-Forwarded-For");
    if (log.isInfoEnabled() && ip != null) {
      log.info("│ X-Forwarded-For= " + ip);
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
        if (log.isInfoEnabled() && ip != null) {
          log.info("│ Proxy-Client-IP= " + ip);
        }
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (log.isInfoEnabled() && ip != null) {
          log.info("│ WL-Proxy-Client-IP= " + ip);
        }
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (log.isInfoEnabled() && ip != null) {
          log.info("│ HTTP_CLIENT_IP= " + ip);
        }
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (log.isInfoEnabled() && ip != null) {
          log.info("│ HTTP_X_FORWARDED_FOR= " + ip);
        }
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
        if (log.isInfoEnabled() && ip != null) {
          log.info("│ IP地址：     " + ip);
        }
      }
    } else if (ip.length() > 15) {
      String[] ips = ip.split(",");
      for (String strIp : ips) {
        if (!("unknown".equalsIgnoreCase(strIp))) {
          ip = strIp;
          break;
        }
      }
    }
    return ip;
  }

}
