package com.qunar.superoa.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *  Cookie 工具类
 *
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 14:18 2019-02-26
 * @Modify by:
 */
@Slf4j
public class CookieUtils {

  public static String getCookie(HttpServletRequest request, String cookieName) {

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        log.info("cookies [ {}: {} ] ({})", cookie.getName(), cookie.getValue(), cookie.getDomain());
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }


  public static void writeCookie(HttpServletResponse response, String cookieName, String value) {
    Cookie cookie = new Cookie(cookieName, value);
    cookie.setPath("/");
    cookie.setMaxAge(3600);
    response.addCookie(cookie);
  }


}
