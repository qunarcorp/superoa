package com.qunar.superoa.controller;

import com.google.gson.internal.LinkedTreeMap;
import com.qunar.superoa.dto.Account;
import com.qunar.superoa.dto.CurrentUserDto;
import com.qunar.superoa.dto.LoginDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.model.Login;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ipml.UserRoleService;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/24_10:15 AM
 * @Despriction: 基础controller
 */

@Slf4j
@Api(value = "MainController", tags = "主入口")
@Controller
public class MainController {

  @Autowired
  private SecurityUtils securityUtils;

  @Autowired
  private UserRoleService userRoleService;

  @Autowired
  private SessionRegistry sessionRegistry;

  @Value("${spring.profiles.active}")
  private String environment;

//  @ApiParam("登录权限信息")
//  @PostMapping("/api/login/account")
//  @ResponseBody
  public Account getAccount(@RequestBody Login login, HttpSession session) {

    securityUtils.login(login.getUserName(), login.getPassword());
    String qtalkID = SecurityUtils.currentUsername();
    Account ac = new Account();
    ac.setType(login.getType());
    ac.setStatus("anonymousUser".equals(qtalkID) ? "error" : "ok");
    GrantedAuthority[] grantedAuthorities = securityUtils.getAuthorities();
    List<String> currentAuthority = new LinkedList<>();
    if (grantedAuthorities.length > 0) {
      for (GrantedAuthority grantedAuthority : grantedAuthorities) {
        currentAuthority.add(grantedAuthority.getAuthority());
      }
    } else if (grantedAuthorities.length == 0) {
      currentAuthority.add("ROLE_USER");
    }
    ac.setCurrentAuthority(currentAuthority);
    return ac;
  }

  @ApiOperation("退出登录")
  @GetMapping("/main/logout")
  @ResponseBody
  public Result logout(HttpServletRequest request, HttpServletResponse response) {
    return ResultUtil.success(securityUtils.logout(request, response));

  }


  @ApiOperation("用户登录")
  @PostMapping("/login")
  @ResponseBody
  public Result login(@RequestBody LoginDto loginDto) {
    if ("prod".equals(environment)) {
      return ResultUtil.error(403, "该接口不可用");
    }
    try {
      securityUtils.login(loginDto.getQtalk(), loginDto.getPassword());
    } catch (AuthenticationException e) {
      return ResultUtil.error(-1, e.getMessage());
    }
    return ResultUtil.success(securityUtils.getUserDetailsObject());
  }

  @ApiOperation("跳转首页")
  @GetMapping("/")
  public String toIndexHtml() {
    return "/index";
  }



  @ApiOperation("切换用户接口")
  @PostMapping("/changeUser")
  @ResponseBody
  public Result changeUser(@RequestBody LoginDto loginDto, HttpServletRequest request) {
    try {
      List<LinkedTreeMap> linkedTreeMaps = SecurityUtils.currentUserInfo().getCurrentAuthority();
      for (LinkedTreeMap linkedTreeMap : linkedTreeMaps) {
        if ("SYSTEM_ADMIN".equals(String.valueOf(linkedTreeMap.get("roleType")))) {
          securityUtils.login(loginDto.getQtalk(), "");
          securityUtils.setSessionTimeOut(request);
          return ResultUtil.success(securityUtils.getUserDetailsObject());
        }
      }
      return ResultUtil.error(403,"无权限");
    } catch (AuthenticationException e) {
      return ResultUtil.error(-1, e.getMessage());
    }
  }


  @ApiOperation("当前登录用户")
  @GetMapping("/currentUser")
  @ResponseBody
  public Result<CurrentUserDto> currentUser() {
    return ResultUtil.success(SecurityUtils.currentUserInfo());
  }

  @ApiOperation("当前登录总量")
  @GetMapping("/currentUserSize")
  @ResponseBody
  public Result currentUserSize() {
    log.error("获取当前登录总量错误");
    log.info("u begin");
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    log.info("${}", allPrincipals.size());
    allPrincipals.forEach(System.out::println);
    //可以转换成spring的User}
    log.info("u end");
    return ResultUtil.success(allPrincipals.size());
  }

  @ApiOperation("healthcheck")
  @GetMapping("/qunartest/check")
  @ResponseBody
  public String healthcheck() {
    return "";
  }
}
