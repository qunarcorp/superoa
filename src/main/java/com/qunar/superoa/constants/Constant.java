package com.qunar.superoa.constants;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/27_8:02 PM
 * @Despriction: 常量
 */

public class Constant {

  /**
   * 表单状态    0:未启用;1:启用;2草稿
   */
  public static final Integer FLOW_UN_START_USED = 0;

  public static final Integer FLOW_START_USED = 1;

  public static final Integer FLOW_DRAFT_FLOW = 2;


  /**
   * 资源文件格式类型
   */
  public static final String FILE_TYPE_ZIP = "zip";

  public static final String FILE_TYPE_BAR = "bar";

  public static final String FILE_TYPE_BPMN = "bpmn";

  public static final String FILE_TYPE_XML = "xml";

  /**
   * flowOrder 流程实例查询类型  0:我发起的;1:我已审批;2:待我审批;3:知会给我的;
   */
  public static final String FLOW_ORDER_MY_QUERY_TYPE = "0";

  public static final String FLOW_ORDER_APPROVED_QUERY_TYPE = "1";

  public static final String FLOW_ORDER_TO_APPROVE_QUERY_TYPE = "2";

  public static final String FLOW_ORDER_NOTIFY_TO_ME_TYPE = "3";

  /**
   * 查询流程实例审批类型  0:我发起的;1:我已审批;2:待我审批
   */
  public static final String FLOW_ORDER_TO_APPROVE = "toApprove";

  public static final String FLOW_ORDER_APPROVED = "approved";

  /**
   * activiti流程状态  1:启动状态;0:挂起状态
   */
  public static final String ACTIVITI_ACTIVE_STATUS = "1";

  public static final String ACTIVITI_SUSPEND_STATUS = "0";

  /**
   * 空格和空串
   */
  public static final String FILL_SPACE = " ";

  public static final String FILL_BLANK = "";

  /**
   * oa员工部门领导相关
   */
  public static final String DEPT_DIRECTOR = "deptDirector";

  public static final String DEPT_VP = "vp";

  public static final String DEPT_HRD = "hrd";

  /**
   * superoa USER 头像默认url地址
   */
  public static final String OA_USER_AVATAR = "https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2019/05/27/11:45:52/005T0OjCly1fukusj7o88j308w08wdg8.jpg";

  /**
   * superoa USER 初始密码
   */
  public static final String OA_USER_ORIGIN_PASSWORD = "******";

  /**
   * superoa USER 上传头像格式
   */
  public static final String UPLOAD_AVATAR_EXTENSION = ".png,.jpg,.jpeg,.gif";

  /**
   * superoa 审批组头像地址
   */
  public static final String OA_GROUP_AVATAR = "https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2018/12/25/16:01:27/team.png";

  /**
   * superoa 审批结束头像地址
   */
  public static final String OA_END_AVATAR = "http://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/lee.guo/2018/11/15/11:10:05/c7d7d5f8f806e17eaa45c7a8be208b5c.png";

  /**
   * qtalk前缀url
   */
  public static final String QT_USER_PREIX = "https://qt.qunar.com/";

  /**
   * qtalk消息通知图标地址
   */
  public static final String QT_NOTICE_IMG = "https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2018/12/25/16:01:34/oa.png";

  /**
   * 二维码公司logo地址
   */
  public static final String QR_QUNAR_LOGO = "https://ops-superoacp.qunarzz.com/ops_superoa_ops_superoa/prod/chengyan.liang/2018/12/25/16:01:42/qunaer.png";

  /**
   * 失效部门标志
   */
  public static final String OA_INVALID_DEPARTMENT = "$D999999";

  /**
   * 发起流程存flowModel和formData缓存key的Variable
   */
  public static final String ACTIVITI_VARIABLE_KEY = "formDataUUid";

  /**
   * 审批类型  consent:同意,forward:转交,counterSign:加签,reject:拒绝,PENDING:加签中,start:发起流程
   */
  public static final String FLOW_CONSENT = "consent";
  public static final String FLOW_CONSENT_Z = "[同意]";

  public static final String FLOW_SYSTEM_MANAGER = "system_manager";
  public static final String FLOW_SYSTEM_MANAGER_Z = "[系统管理员处理]";

  public static final String FLOW_FORWARD = "forward";
  public static final String FLOW_FORWARD_Z = "[转交]";
  public static final String FLOW_FORWARD_USER = "转交人";

  public static final String FLOW_COUNTERSIGN = "counterSign";
  public static final String FLOW_COUNTERSIGN_Z = "[加签]";
  public static final String FLOW_COUNTERSIGN_USER = "加签人";
  public static final String FLOW_COUNTERSIGN_ING = "PENDING";

  public static final String FLOW_REJECT = "reject";
  public static final String FLOW_REJECT_Z = "[拒绝]";

  public static final String FLOW_START = "start";
  public static final String FLOW_START_Z = "发起流程";
  public static final String FLOW_START_NODE = "开始节点";

  public static final String FLOW_REVOKE = "revoke";
  public static final String FLOW_REVOKE_Z = "撤销流程";
  public static final String FLOW_REVOKE_NODE = "申请人撤销流程";
  public static final String FLOW_CAN_REVOKE = "canRevoke";


  public static final String FLOW_DRAFT = "draft";
  public static final String FLOW_DRAFT_Z = "草稿";
  public static final String FLOW_DRAFT_START = "draftStart";
  public static final String FLOW_EXTERNAL_START = "externalStart";

  public static final String FLOW_NO_APPROVE_USERS = "[无审批人自动通过]";
  public static final String FLOW_APPROVE_USERS_REP = "[同一审批人自动通过]";

  /**
   * 管理员替换审批人操作类型
   */
  public static final String FLOW_UPDATE_APPROVER = "updateApprover";
  public static final String FLOW_ADD_APPROVER = "addApprover";
  public static final String FLOW_DELETE_APPROVER = "delApprover";

  /**
   * flowMolde 节点可编辑字段  字段属性
   */
  public static final String FLOW_MODEL_EDIT_NODE_REQUIRED = "required";

  /**
   * flowMolde 节点节点属性字段  字段属性
   */
  public static final String FLOW_MODEL_NODE_PROPERTY_NOT_PASS = "notPass";

  /**
   * 流程动作 start:发起流程;approve:审批
   */
  public static final String FLOW_ORDER_START = "start";

  public static final String FLOW_ORDER_APPROVE = "approve";

}
