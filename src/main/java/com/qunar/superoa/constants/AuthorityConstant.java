package com.qunar.superoa.constants;

/**
 * @Auther: chengyan.liang
 * @Despriction: 权限校验常量
 * @Date:Created in 2:51 PM 2019/5/9
 * @Modify by:
 */
public class AuthorityConstant {

  /**
   * 外部系统模版接入接口
   */
  public static final String EXT_SYS_FORM = "/fromExternal/";

  /**
   * 外部系统流程待审批接入接口
   */
  public static final String EXT_SYS_UNAPPROVE_FLOW = "/extSysUnapproveFlow/";

  /**
   * 外部系统流程发起接入接口
   */
  public static final String EXT_SYS_FLOW_MODEL = "/extSysFlowModel/";

  /**
   * 所有免登录操作
   */
  public static final String NO_NEED_LOGIN = "toIndexHtml,login,logout,dologin,qmonitor,html,healthcheck,details,detailsAllInfo,approve,reject,history";

  /**
   * 外部系统流程待审批、发起 更新、删除接口方法 - 严格IP限制(包括登录时)
   */
  public static final String STRICT_EXT_SYS_FLOW = "update,remove,update_patch";

}
