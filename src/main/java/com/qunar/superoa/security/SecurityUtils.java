package com.qunar.superoa.security;

import com.google.gson.internal.LinkedTreeMap;
import com.qunar.superoa.dto.CurrentUserDto;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_6:21 PM
 * @Despriction: 提供了登陆的接口 login()，还有一些和登陆有关的方法
 */

@Slf4j
@Service
public class SecurityUtils {

  private final AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;

  @Value("${api.property.sessiontimeout}")
  private String sessionTimeOut;

  @Autowired
  public SecurityUtils(
      @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }


  /**
   * 判断当前用户是否已经登陆
   *
   * @return 登陆状态返回 true, 否则返回 false
   */
  public static boolean isLogin() {
    String qtalk = SecurityContextHolder.getContext().getAuthentication().getName();
    return !"anonymousUser".equals(qtalk);
  }

  /**
   * 取得登陆用户的 ID, 如果没有登陆则返回 -1
   *
   * @return 登陆用户的 ID
   */
  public static String getLoginUserId() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (SecurityUtils.isLogin()) {
      MySuperOAUserDetails myUserDetails = (MySuperOAUserDetails) principle;
      return myUserDetails.getUsername();
    }

    return null;
  }


  public static String getCname() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (SecurityUtils.isLogin()) {
      MySuperOAUserDetails myUserDetails = (MySuperOAUserDetails) principle;
      return myUserDetails.getCname();
    }

    return null;
  }

  public void login(String qtalk, String password) {
    // 内部登录请求
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(qtalk,
        password, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
    // 验证
    Authentication auth = authenticationManager.authenticate(authRequest);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  public String logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return "退出成功";
  }

  /**
   * 重设session过期时间
   */
  public void setSessionTimeOut(HttpServletRequest request) {
    HttpSession session = request.getSession();
    session.setMaxInactiveInterval(Integer.valueOf(sessionTimeOut));
  }

  /**
   * 登陆重定向
   *
   * @param qtalk Qtalk账号
   * @param password 密码
   * @return 登陆后需要访问的页面的 URL
   */
  public String loginRedirect(String qtalk, String password) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes()).getRequest();
    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes()).getResponse();
    // 默认登陆成功的页面
    String defaultTargetUrl = "/";
    // 默认为登陆错误页面
    String redirectUrl = "/login?error=1";

    try {
      Authentication token = new UsernamePasswordAuthenticationToken(qtalk, password);
      // 登陆
      token = authenticationManager.authenticate(token);
      SecurityContextHolder.getContext().setAuthentication(token);
//            tokenBasedRememberMeServices.onLoginSuccess(request, response, token); // 使用 remember me
      // 重定向到登陆前的页面
      SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
      redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : defaultTargetUrl;
    } catch (Exception ex) {
      log.info(ex.getMessage());
    }

    return redirectUrl;
  }

  /**
   * @return 返回当前登录用户的信息
   */
  public static Authentication currentUser() {

    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * @return 返回当前登录用户的信息
   */
  public static CurrentUserDto currentUserInfo() {
    return new CurrentUserDto(
        SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  /**
   * @return userDetails
   */
  public org.springframework.security.core.userdetails.UserDetails getUserDetails() {
    if (!"anonymousUser".equals(currentUsername())) {
      return (org.springframework.security.core.userdetails.UserDetails) SecurityContextHolder
          .getContext().getAuthentication().getPrincipal();
    }
    return null;
  }

  public Object getUserDetailsObject() {
    if (getUserDetails() == null) {
      return getAuthorities();
    }
    return getUserDetails();
  }

  /**
   * 获取权限
   *
   * @return 权限list
   */
  public GrantedAuthority[] getAuthorities() {
    if (!"anonymousUser".equals(currentUsername())) {
      return getUserDetails().getAuthorities().toArray(new GrantedAuthority[0]);
    }
    return null;
  }

  /**
   * @return 返回当前登录Qtalk账号
   */
  public static String currentUsername() {
    try {
      return currentUser().getName();
    } catch (Exception e) {
      return null;
    }
  }


  public void logInAs(String username) {

    UserDetails user = userDetailsService.loadUserByUsername(username);
    if (user == null) {
      throw new IllegalStateException(
          "SuperOAUser " + username + " doesn't exist, please provide a valid user");
    }
    log.info("> Logged in as: " + username);
    SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
      }

      @Override
      public Object getCredentials() {
        return user.getPassword();
      }

      @Override
      public Object getDetails() {
        return user;
      }

      @Override
      public Object getPrincipal() {
        return user;
      }

      @Override
      public boolean isAuthenticated() {
        return true;
      }

      @Override
      public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

      }

      @Override
      public String getName() {
        return user.getUsername();
      }
    }));
    org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(username);
  }

  /**
   * 用户管理员权限校验,管理员返回 true,非管理员返回 false
   */
  public static boolean checkSysAndActivitiAdmin() {
    List<LinkedTreeMap> linkedTreeMaps = SecurityUtils.currentUserInfo().getCurrentAuthority();
    boolean isSystemAdmin = false;
    boolean isActivitiAdmin = false;
    for (LinkedTreeMap linkedTreeMap : linkedTreeMaps) {
      if ("SYSTEM_ADMIN".equalsIgnoreCase(String.valueOf(linkedTreeMap.get("roleType")))) {
        isSystemAdmin = true;
      }
      if ("ACTIVITI_ADMIN".equalsIgnoreCase(String.valueOf(linkedTreeMap.get("roleType")))) {
        isActivitiAdmin = true;
      }
    }
    return isSystemAdmin && isActivitiAdmin;
  }

  /**
   * 用户系统管理员权限校验,系统管理员返回 true,非系统管理员返回 false
   */
  public static boolean checkSysAdmin() {
    List<LinkedTreeMap> linkedTreeMaps = SecurityUtils.currentUserInfo().getCurrentAuthority();
    boolean isSystemAdmin = false;
    for (LinkedTreeMap linkedTreeMap : linkedTreeMaps) {
      if ("SYSTEM_ADMIN".equalsIgnoreCase(String.valueOf(linkedTreeMap.get("roleType")))) {
        isSystemAdmin = true;
      }
    }
    return isSystemAdmin;
  }

  /**
   * 用户流程管理员权限校验,流程管理员返回 true,非流程管理员返回 false
   */
  public static boolean checkActivitiAdmin() {
    List<LinkedTreeMap> linkedTreeMaps = SecurityUtils.currentUserInfo().getCurrentAuthority();
    boolean isActivitiAdmin = false;
    for (LinkedTreeMap linkedTreeMap : linkedTreeMaps) {
      if ("ACTIVITI_ADMIN".equalsIgnoreCase(String.valueOf(linkedTreeMap.get("roleType")))) {
        isActivitiAdmin = true;
      }
    }
    return isActivitiAdmin;
  }
}
