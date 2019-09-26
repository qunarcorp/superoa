package com.qunar.superoa.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {

  UNKOWN_ERROR(-1, "未知错误"),
  SUCCESS(0, "成功"),
  PRIMARY_SCHOOL(100, "你可能还在上小学"),
  MIDDLE_SCHOOL(101, "你可能还在上初中"),
  LOGIN_ERROR(-100, "登录信息错误"),
  UPLOAD_ERROR(403, "匿名用户禁止上传附件"),
  GET_CURRENT_USER_ERROR(403, "未登录"),
  LOGIN_OTHER(403, "账号已在其他地方登录！"),
  PARAMETER_ERROR(-100, "参数错误"),
  EMPTY_USERNAME(-1001, "用户名不能为空"),
  ROLETYPE_ERROR(-1002, "请输入正确的角色名称"),
  REPETITION_ROLETYPE(-1003, "userName已拥有roleType角色"),
  NO_OWN_ROLETYPE(-1004, "该用户未拥有此角色"),
  NO_ADMIN(-1005, "该用户是普通用户，未拥有管理员角色"),
  NO_USER(-1006, "当前无登录用户"),

  //AgentException
  DATE_FORMART_ERROR(-2001, "日期格式错误, eg: yyyy-MM-dd HH:mm:ss"),

  //InvokeException
  INVOKE_ERROR(-3001, "反射发生错误，请检查后台代码！"),

  FLOW_MODEL_NAME(-4001, "流程模板名称不能重复"),
  FLOW_MODEL_KEY(-4002, "流程模板key不能重复"),
  FLOW_MODEL_NAME_NULL(-4003, "流程模版名称不能为空"),
  FLOW_MODEL_NAME_ERROR(-4004, "流程模板名称不存在"),
  FLOW_MODEL_DEPT_NULL(-4005, "流程模版部门不能为空"),
  FLOW_MODEL_NOT_EXISTS(-4006, "流程模版不存在"),
  FLOW_MODEL_KEY_NULL(-5001, "流程模板key不能为空"),
  FLOW_MODEL_MODEL_JSON_GROUPS(-4007, "流程模板内容(groups)不能为空"),
  FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS(-4008, "流程分支条件复选框选项内容不能为空"),
  FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS_CONDITION_LINK(-4008,
      "流程分支条件复选框关联条数需和选项option数目相同"),
  FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS_CONDITION_LINK_EQUAL(-4008,
      "流程分支条件复选框关联Code的条件需和选项option对应并相同"),
  FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE(-4009, "流程分支条件关联选项linkCode不能为空"),
  FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE_WRONG(-4010, "流程分支条件关联选项linkCode格式填写错误"),
  FLOW_MODEL_MODEL_JSON_CONDITION_DEPARTMENT_LEVEL_WRONG(-4011, "申请人部门作为流程条件需选择条件部门级别"),


  FLOW_ORDER_NOT_NULL(-6004, "流程Id不能为空"),
  FLOW_ORDER_FORWARD_USERID_NOT_NULL(-6005, "流程转交人不能为空"),
  FLOW_ORDER_COUNTERSIGN_USERID_NOT_NULL(-6003, "流程加签人不能为空"),
  FLOW_ORDER_NOT_QUERY(-6006, "无此流程查看权限"),
  FLOW_ORDER_NOT_APPROVE(-6007, "无此流程审批权限"),
  FLOW_ORDER_NOT_NOTIFY_APPROVE(-6008, "无此流程催办权限"),
  FLOW_ORDER_APPROVE_NOT_QUERY(-6009, "此流程已被其他审批人审批"),
  FLOW_COUNT_FORWARD(-6010, "流程加签状态不能转交"),
  FLOW_COUNT_COUNTER(-6011, "流程加签状态不能多次加签"),
  FLOW_COUNT_COUNTER_TO_SELF(-6012, "不可以加签给自己"),
  FLOW_COUNT_FORWARD_TO_SELF(-6013, "不可以转交给自己"),
  FLOW_ORDER_APPROVED(-6014, "此流程节点已被审批"),
  FLOW_ORDER_UPDATE_NODE(-6015, "无此流程修改权限"),
  FLOW_ORDER_UPDATE_NODE_USER(-6016, "当前任务待审批人无此人"),
  FLOW_ORDER_OPERATE_TYPE(-6017, "更新节点审批人操作类型错误"),
  FLOW_ORDER_OPERATE_DELETE(-6018, "删除节点审批人后结果可能为空"),
  FLOW_ORDER_REVOKE_NOT_APPLY_USER(-6019, "非流程发起者不能撤销"),
  FLOW_ORDER_REVOKE_APPROVED(-6020, "流程已被审批，无法撤销"),


  FLOW_ORDER_USER_NOT_NULL(-6023, "发起人不能为空"),
  FLOW_ORDER_APPCODE_NOT_NULL(-6024, "appCode不能为空"),
  FLOW_ORDER_FLOW_KEY_NOT_NULL(-6025, "流程Key不能为空"),
  FLOW_ORDER_VERSION_NOT_NULL(-6026, "版本号不能为空"),

  ACL_MANAGE_IP_NULL(403, "ip限制，请先申请该机器访问权限"),
  ACL_MANAGE_API_NULL(403, "接口限制，请先申请该接口访问权限"),
  ACL_MANAGE_FLOW_NULL(403, "流程限制，请先申请该流程访问权限"),


  GROUP_IS_HAVE(-8001, "该工作组名称已经存在"),

  ENCRYPT_TIMESTAMP_ERROR(-9001, "时间戳转换失败"),

  ACTIVITI_FLOW_NOT(-7001, "流程不存在"),
  ACTIVITI_FLOW_IMG_NOT(-7003, "流程图不存在"),
  ACTIVITI_FLOW_DELETE_ERROR(-7002, "流程移除失败"),
  ACTIVITI_MODEL_TO_JSONNODE_ERROR(-7004, "流程model转换为JsonNode失败"),
  ACTIVITI_MODEL_TO_XML_ERROR(-7004, "流程转换为xml失败"),
  ACTIVITI_MODEL_NULL(-7005, "模型数据为空，请先设计流程并成功保存，再进行发布"),
  ACTIVITI_MODEL_ERROR(-7005, "流程模型不符要求，请至少设计一条主线流程"),


  FLOW_MODEL_EDITNODE_ERROE(-8001, "流程模板editNode字段json格式错误"),
  FLOW_MODEL_KEY_NOT_NULL(-5001, "流程模板key不能为空"),
  FLOW_MODEL_KEY_ERROR(-5002, "流程模板key不存在"),
  FLOW_MODEL_STATUS_ERROR(-5010, "流程模板未激活"),
  FLOW_LEADER_ERROR(-5003, "获取直属leader异常,新入职同学请明日申请"),
  FLOW_LEADER_IS_NULL(-5004, "获取直属leader为空,新入职同学请明日申请"),
  FLOW_VP_ERROR(-5011, "获取部门VP失败"),
  FLOW_DATA_ERROR(-5012, "获取节点审批人条件失败"),
  FLOW_DIRECTOR_ERROR(-5013, "获取部门总监失败"),
  FLOW_HRD_ERROR(-5014, "获取人力资源总监失败"),
  FLOW_APPROVEUSERS_ERROR(-5015, "待审批人离职或格式填写错误"),
  FLOW_HRBP_ERROR(-5006, "获取HRBP异常"),
  FLOW_HRBP_IS_NULL(-5007, "获取HRBP为空"),
  FLOW_DEPT_ERROR(-5008, "获取部门异常"),
  FLOW_DEPT_IS_NULL(-5009, "获取部门为空"),
  FLOW_USER_INFO_FAIL(-5010, "请求员工汇报线信息接口失败"),
  FLOW_USER_INFO_ERROR(-5010, "获取员工汇报线信息失败"),
  FLOW_APPROVE_USERS_IS_NULL_ADD(-5005, "下一节点审批人为空，请填写下一节点审批人"),
  FLOW_APPROVE_USERS_IS_NULL(-5005, "下一节点审批人列表为空");

  private Integer code;

  private String msg;

  ResultEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
