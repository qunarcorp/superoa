package com.qunar.superoa.aop;

import com.qunar.superoa.constants.AuthorityConstant;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.ACLManageException;
import com.qunar.superoa.exceptions.SuperOALoginException;
import com.qunar.superoa.model.ACLManage;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ACLManageServiceI;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.NetworkUtil;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/24_10:19 AM
 * @Despriction: 记录信息
 */

@Aspect
@Component
@Slf4j
public class HttpAspect {

  @Pointcut("execution(public * com.qunar.superoa.controller.*.*(..))")
  public void log() {
  }

  @Pointcut("execution(public * com.qunar.superoa.service.*.*(..))")
  public void serviceTimeArround() {
  }

  @Pointcut("execution(public * com.qunar.superoa.controller.*.*(..))")
  public void controllerTimeArround() {
  }

  @Autowired
  private ACLManageServiceI aclManageServiceI;

  @Before("log()")
  public void doBefore(JoinPoint joinPoint) {
    String currentUser = SecurityUtils.currentUsername();
    log.debug("\n\n\n");
    log.debug(
        "┌──────────────────────────────────────────────────────────────────────────────────────────────────");
    log.debug("│ 当前用户：    {}", currentUser);
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    //ip
    NetworkUtil.getIpAddress(request);
    //url
    log.debug("│ URL：       {}", request.getRequestURI());
    //method
    log.debug("│ 请求方式:    {}", request.getMethod());
    //类方法
    log.debug("│ 加载类：     {}",
        joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    //参数
    log.debug("│ 参数：       {}", joinPoint.getArgs());
    if ("anonymousUser".equalsIgnoreCase(currentUser)) {
      //外部系统模版接入免登录，IP验证
      if (request.getRequestURI().startsWith(AuthorityConstant.EXT_SYS_FORM)) {
        checkIpAndUrl(request.getRequestURI().replaceAll(AuthorityConstant.EXT_SYS_FORM, ""), NetworkUtil.getIpAddress(request));
        //外部系统流程发起接入免登录，IP验证
      } else if (request.getRequestURI().startsWith(AuthorityConstant.EXT_SYS_FLOW_MODEL)) {
        checkIpAndUrl(request.getRequestURI().replaceAll(AuthorityConstant.EXT_SYS_FLOW_MODEL, ""), NetworkUtil.getIpAddress(request));
        //外部系统流程待审批接入免登录，IP验证
      } else if (request.getRequestURI().startsWith(AuthorityConstant.EXT_SYS_UNAPPROVE_FLOW)) {
        checkIpAndUrl(request.getRequestURI().replaceAll(AuthorityConstant.EXT_SYS_UNAPPROVE_FLOW, ""), NetworkUtil.getIpAddress(request));
      } else if (!AuthorityConstant.NO_NEED_LOGIN.toLowerCase().contains(joinPoint.getSignature().getName().toLowerCase())
      ) {
        throw new SuperOALoginException(ResultEnum.GET_CURRENT_USER_ERROR);
      }
      //登陆时，同样校验外部系统待审批、发起IP限制
    } else if (request.getRequestURI().startsWith(AuthorityConstant.EXT_SYS_FLOW_MODEL) || request.getRequestURI().startsWith(AuthorityConstant.EXT_SYS_UNAPPROVE_FLOW)) {
      String url = request.getRequestURI().replaceAll(AuthorityConstant.EXT_SYS_FLOW_MODEL, "").replaceAll(AuthorityConstant.EXT_SYS_UNAPPROVE_FLOW, "");
      if (AuthorityConstant.STRICT_EXT_SYS_FLOW.toLowerCase().contains(url.toLowerCase())) {
        checkIpAndUrl(url, NetworkUtil.getIpAddress(request));
      }
    }
  }

  /**
   * IP 接口 验证
   * @param url 访问接口名称
   * @param ip 机器IP
   */
  private void checkIpAndUrl(String url, String ip) {
    Optional<ACLManage> aclManage = aclManageServiceI.getAclManageByIp(ip);
    if (aclManage.isPresent()) {
      if (!aclManage.get().getApis().contains(url.split("/")[0])) {
        throw new ACLManageException(ResultEnum.ACL_MANAGE_API_NULL);
      }
    } else {
      throw new ACLManageException(ResultEnum.ACL_MANAGE_IP_NULL);
    }
  }

  @After("log()")
  public void doAfter() {
  }

  @AfterReturning(returning = "object", pointcut = "log()")
  public void doAfterReturning(Object object) {
    log.debug("│ 返回结果：    {}", object);
    log.debug(
        "└──────────────────────────────────────────────────────────────────────────────────────────────────");
  }

  /**
   * @param joinPoint 切点
   * @return 统计方法执行耗时Around环绕通知
   */
  @Around("controllerTimeArround()")
  public Object timeAroundController(ProceedingJoinPoint joinPoint) throws Throwable, Exception {
    return timeAround(joinPoint);
  }

  //  @Around("serviceTimeArround()")
  public Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable, Exception {
    // 定义返回对象、得到方法需要的参数
    Object obj;
    Object[] args = joinPoint.getArgs();
    long startTime = System.currentTimeMillis();

    obj = joinPoint.proceed(args);

    // 获取执行的方法名
    long endTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

    // 打印耗时的信息
    CommonUtil.printExecTime(methodName, startTime, endTime);

    return obj;
  }

}
