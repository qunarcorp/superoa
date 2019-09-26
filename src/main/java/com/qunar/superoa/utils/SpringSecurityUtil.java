package com.qunar.superoa.utils;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpSession;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_5:33 PM
 * @Despriction:
 */

public class SpringSecurityUtil {

    //session 由controller 注入参数传入
    public static String currentUser(HttpSession session) {
        SecurityContextImpl securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
        return ((UserDetails)securityContext.getAuthentication().getPrincipal()).getUsername();
    }


}
