package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qunar.superoa.cache.Cache;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.dao.GroupRepository;
import com.qunar.superoa.dao.SuperOAUserRepository;
import com.qunar.superoa.dto.ExternalQueryDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.dto.MailInfo;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.*;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.*;
import com.qunar.superoa.thread.RemindApproveUsersRunnable;
import com.qunar.superoa.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xing.zhou on 2018/8/31. Modified by chengyan.liang on 2018/10/9 Modified by lee.guo on 2018/10/31
 */
@Service
@Slf4j
public class FlowServiceImpl implements FlowServiceI {

  @Autowired
  private FlowModelServiceI flowModelServiceI;

  @Autowired
  private FormDataServiceI formDataServiceI;

  @Autowired
  private NotifyServiceI notifyServiceI;

  @Autowired
  private ActivitiServiceI activitiServiceI;

  @Autowired
  private FlowOrderServiceI flowOrderServiceI;

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  @Autowired
  private ApproveLogServiceI approveLogServiceI;

  @Autowired
  private AgentServiceI agentServiceI;

  @Autowired
  private TaskService taskService;

  @Autowired
  private JsonMapUtil jsonMapUtil;

  @Autowired
  private UserInfoUtil userInfoUtil;

  @Autowired
  private SuperOAUserRepository superOAUserRepository;

  @Autowired
  private OpsappApiServiceI opsappApiServiceI;

  @Autowired
  private ProcessDiagramGenerator processDiagramGenerator;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private MinioUtils minioUtils;

  @Autowired
  private FlowNodeDataServiceI flowNodeDataServiceI;

  @Autowired
  private ACLManageServiceI aclManageServiceI;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private RemindApproveUsersRunnable remindApproveUsersRunnable;

  @Value("${isSendQtalk}")
  private String isSendQtalk;

  @Value("${api.isapi.qunaroa}")
  private String qunaroaApi;

  @Value("${api.isapi.qtalkoa}")
  private String qtalkoaApi;

  /**
   * 存草稿
   */
  @Override
  @Transactional
  public String notStartProcessInstance(FormDataDto formDataDto) {
    String currentUser = SecurityUtils.currentUsername();
    String currentUserName = SecurityUtils.currentUserInfo().getCname();
    String flowKey = formDataDto.getFlowKey();
    //检测
    FlowModel flowModel = checkStartFlow(flowKey, null, Constant.FLOW_DRAFT, null, null);
    if (formDataDto.getProcInstId().isEmpty()) {
      //保存数据
      FormData formData = formDataServiceI.addFromData(formDataDto, flowModel, false);
      //添加流程数据   formDataId设置为flowOrder 的prodInstId
      FlowOrder flowOrder = createFlowOrderDraft(flowModel, formData.getId(), currentUser,
          currentUserName);
      flowOrderServiceI.save(flowOrder);
    } else {
      //保存数据
      formDataServiceI.addFromData(formDataDto, flowModel, true);
    }
    return "存草稿成功";
  }

  /**
   * 创建流程订单草稿
   */
  private FlowOrder createFlowOrderDraft(FlowModel flowModel, String prodInstId, String currentUserId, String currentUserName) {
    FlowOrder flowOrder = new FlowOrder();
    flowOrder.setProcInstId(prodInstId);
    flowOrder.setApplyUserId(currentUserId);
    flowOrder.setApplyStatus(3);
    flowOrder.setApplyTime("");
    flowOrder.setApplyTypeKey(flowModel.getFormKey());
    flowOrder.setApplyTypeName(flowModel.getFormName());
    flowOrder.setApplyUserName(currentUserName);
    flowOrder.setHeadline(currentUserName + "的" + flowModel.getFormName());
    flowOrder.setLogOid("");
    flowOrder.setNextApproveUsers("");
    flowOrder.setApprovedUsers(",");
    flowOrder.setLastApproveTime("");
    flowOrder.setQueryUsers("," + currentUserId + ",");
    flowOrder.setUpdateTime(DateTimeUtil.getDateTime());
    return flowOrder;
  }

  /**
   * 发起草稿流程
   */
  @Override
  @Transactional
  public String startDraftFlow(FormDataDto formDataDto) {
    String currentUserId = SecurityUtils.currentUsername();
    String currentUserName = SecurityUtils.currentUserInfo().getCname();
    return startFlow(formDataDto, Constant.FLOW_DRAFT_START, currentUserId, currentUserName, "oa", null, null);
  }

  /**
   * 发起流程 来自外部系统
   */
  @Override
  @Transactional
  public String startFromExternal(ExternalQueryDto externalQueryDto, String ip) {
    FormDataDto formDataDto = new FormDataDto();
    formDataDto.setFlowKey(externalQueryDto.getFormKey());
    formDataDto.setFormDatas(externalQueryDto.getFormDatas());
    return startFlow(formDataDto, Constant.FLOW_EXTERNAL_START, externalQueryDto.getCurrentUserId(),
        externalQueryDto.getCurrentUserName(), externalQueryDto.getAppCode(), externalQueryDto.getVersion(), ip);
  }

  /**
   * 发起流程 本系统
   */
  @Override
  @Transactional
  public String startProcessInstance(FormDataDto formDataDto, String type) {
    String currentUserId = SecurityUtils.currentUsername();
    String currentUserName = SecurityUtils.currentUserInfo().getCname();
    return startFlow(formDataDto, "", currentUserId, currentUserName, "oa", null, null);
  }

  /**
   * 发起流程
   */
  private String startFlow(FormDataDto formDataDto, String type, String currentUserId, String currentUserName, String appCode, String version, String ip) {
    String flowKey = formDataDto.getFlowKey();
    //检测是否可以发起  可以发起返回flowModel
    FlowModel flowModel = checkStartFlow(flowKey, formDataDto, type, version, ip);
    //获取节点分支条件数据存入流程
    String[] Variables = getKeyValue(flowModel, formDataDto.getFormDatas(), Constant.FLOW_ORDER_START, currentUserId);
    log.info("Variables:{}", Variables);
    //发起流程
    String procInstId = activitiServiceI.startProcessInstance(flowKey, Variables[0], Variables[1], currentUserId);
    //获取知会人
    String notifyUsers = getNotifyUsers(procInstId);
    formDataDto.setProcInstId(procInstId);
    //保存数据
    FormData formData = formDataServiceI.addFromData(formDataDto, flowModel, false);
    //添加流程数据
    FlowOrder flowOrder = createFlowOrder(procInstId, flowModel, currentUserId, currentUserName, notifyUsers,
        formDataDto.getProcInstId(), Variables[1], appCode);
    //添加log
    approveLogServiceI.createApproveLog(procInstId, "", "begin", Constant.FLOW_START_NODE,
        flowModel.getFormName(), Constant.FLOW_START, Constant.FLOW_START_Z, currentUserId, currentUserName);
    //存储当前节点流程表单数据
    flowNodeDataServiceI.saveFlowNodeData(currentUserId, currentUserName, formData.getFormDatas(), flowKey,
        flowModel.getFormName(), "begin", "开始节点", procInstId);
    //发送知会通知
    sendNotify(flowOrder, notifyUsers, "", "你", currentUserId, 1,
        flowOrder.getNextApproveUsers(), Constant.FLOW_START, "");
    return "发起流程成功";
  }

  /**
   * 检测flowkey是否可以发起该流程
   */
  private FlowModel checkStartFlow(String flowKey, FormDataDto formDataDto, String type, String version, String ip) {
    if (flowKey.isEmpty()) {
      throw new FlowException(ResultEnum.FLOW_MODEL_KEY_NULL);
    }
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKey(flowKey);
    //外部发起  可发起老版本流程
    if (Constant.FLOW_EXTERNAL_START.equals(type)) {
      if (!String.valueOf(flowModel.getFormVersion()).equals(version.trim())) {
        flowModel = flowModelServiceI.getFlowModelByFormKeyAndVersion(flowKey, version);
        //发送通知给管理员和负责人
        notifyAppCodeUsers(ip, flowModel.getFormName(), flowKey);
      }
    } else {
      if (flowModel.getFlowStatus() != 1) {
        throw new FlowException(ResultEnum.FLOW_MODEL_STATUS_ERROR);
      }
    }
    if (flowModel == null) {
      throw new FlowException(ResultEnum.FLOW_MODEL_KEY_ERROR);
    }
    //草稿发起  必填processInstId
    if (Constant.FLOW_DRAFT_START.equals(type)) {
      if (StringUtils.isBlank(formDataDto.getProcInstId())) {
        throw new FlowException(ResultEnum.FLOW_ORDER_NOT_NULL);
      }
    }
    if (Constant.FLOW_DRAFT.equals(type)) {
      return flowModel;
    }
    if (!checkFormData(flowModel, formDataDto)) {
      throw new FlowException(ResultEnum.FLOW_APPROVEUSERS_ERROR);
    }
    return flowModel;
  }

  /**
   * 外部发起低版本流程，通知负责人和管理员
   */
  private void notifyAppCodeUsers(String ip, String flowName, String flowKey) {
    //获取负责人
    ACLManage aclManage = aclManageServiceI.getAclManageByIp(ip).get();
    //系统管理员
    Optional<WorkGroup> workGroup = groupRepository.findByName("superman");
    String toUsers = workGroup.get().getMembers() + "," + aclManage.getUsers();

    OpsappSendMessageInfo opsappSendMessageInfo = new OpsappSendMessageInfo();
    opsappSendMessageInfo.setImg(Constant.QT_NOTICE_IMG);
    opsappSendMessageInfo.setType("667");
    opsappSendMessageInfo.setSystem("worknotice");
    opsappSendMessageInfo.setTitle("流程版本号过期提示");
    opsappSendMessageInfo.setContent("流程:" + flowName + "版本号已发生变化，请联系管理员修改发起信息。");
    List<String> qtalkList = Arrays.asList(toUsers.split(","));
    opsappSendMessageInfo.setQtalk_ids(qtalkList);
    opsappSendMessageInfo.setBody("流程:" + flowName + "版本号已发生变化，请联系管理员修改发起信息。");
    opsappSendMessageInfo.setLinkurl(qunaroaApi + "/cooperate/create/" + flowKey);
    //若配置文件显示不发送qtalk，则直接返回发送成功
    if ("false".equalsIgnoreCase(isSendQtalk)) {
      return;
    } else {
      opsappApiServiceI.sendQtalkMessage(opsappSendMessageInfo);
    }
  }

  /**
   * 检测表单填写的待审批人是否符合规范或在职
   */
  private Boolean checkFormData(FlowModel flowModel, FormDataDto formDataDto) {
    final Boolean[] formStatus = {true};
    String nodeApproveUsers = flowModel.getNodeApproveUsers();
    Map<String, String> nodeApproveUsersMap = Maps.newHashMap();
    //获取flowMode中的应填写的待审批人的节点名称
    if (StringUtils.isNotBlank(nodeApproveUsers)) {
      nodeApproveUsersMap = new Gson().fromJson(nodeApproveUsers, Map.class);
    }

    for (Map.Entry<String, String> nodeEntry : nodeApproveUsersMap.entrySet()) {
      String userNames = JsonMapUtil.getValueByKey(nodeEntry.getValue(), formDataDto.getFormDatas())
          .replaceAll(Constant.FILL_SPACE, Constant.FILL_BLANK).replaceAll("\\[", Constant.FILL_BLANK).replaceAll("]", Constant.FILL_BLANK);
      Arrays.stream(userNames.split(",")).forEach(userName -> {
        if ("无".equals(userName) || StringUtils.isBlank(userName)) {
          formStatus[0] = true;
        } else {
          List<SuperOAUser> oaUserList = Lists.newArrayList();
          try {
            oaUserList = superOAUserRepository.findByUserName(userName);
          } catch (Exception e) {
            formStatus[0] = false;
            return;
          }
          if (oaUserList.isEmpty()) {
            formStatus[0] = false;
            return;
          }
        }
      });
      if (!formStatus[0]) {
        return false;
      }
    }
    return formStatus[0];
  }

  /**
   * 从模板数据中取分支条件的数据
   */
  private String[] getKeyValue(FlowModel flowModel, Map<String, Object> formDatas, String type, String currentUserId) {
    String keys = "";
    String values = "";
    Map<String, String> map = new HashMap<>();
    if (flowModel.getFormBranchConditions() != null && !""
        .equals(flowModel.getFormBranchConditions())) {
      map = new Gson().fromJson(flowModel.getFormBranchConditions(), Map.class);
    }
    for (String key : map.keySet()) {
      keys += key + ",";
      values += jsonMapUtil.getValueByMap(map.get(key), formDatas, currentUserId) + ",";
    }
    if (Constant.FLOW_ORDER_START.equals(type)) {
      String uuid = UUID.randomUUID().toString();
      keys += Constant.ACTIVITI_VARIABLE_KEY + ",";
      values += uuid + ",";
      Cache.setFlowDataCache(uuid, formDatas);
      Cache.setFlowDataCache(uuid + "model", flowModel);
    }
    return new String[]{keys, values};
  }

  /**
   * 创建流程订单
   */
  private FlowOrder createFlowOrder(String prcoInstId, FlowModel flowModel, String currentUserId,
                                    String currentUserName, String notifyUsers, String flowId, String uuids, String appCode) {
    FlowOrder flowOrder = new FlowOrder();
    flowOrder.setProcInstId(prcoInstId);
    flowOrder.setApplyUserId(currentUserId);
    flowOrder.setApplyUserDept(userInfoUtil.getUserDept(currentUserId));
    flowOrder.setApplyUserFullDept(userInfoUtil.getUserAllDept(currentUserId));
    flowOrder.setApplyStatus(1);
    flowOrder.setApplyTime(DateTimeUtil.getDateTime());
    flowOrder.setApplyTypeKey(flowModel.getFormKey());
    flowOrder.setApplyTypeName(flowModel.getFormName());
    flowOrder.setApplyUserName(currentUserName);
    flowOrder.setHeadline(currentUserName + "的" + flowModel.getFormName());
    flowOrder.setLogOid(prcoInstId);
    String nextAproveUsers = activitiServiceI.getApproveNestUsers(prcoInstId, null);
    flowOrder.setNextApproveUsers(nextAproveUsers);
    flowOrder.setApprovedUsers(",");
    flowOrder.setLastApproveTime("");
    flowOrder.setQueryUsers(nextAproveUsers + notifyUsers + "," + currentUserId + ",");
    flowOrder.setAppCode(appCode);
    if (StringUtils.isNotBlank(notifyUsers)) {
      flowOrder.setNotifyUsers(notifyUsers);
    }
    flowOrder.setUpdateTime(DateTimeUtil.getDateTime());
    if (StringUtils.isNotBlank(flowId)) {
      flowOrder.setId(flowId);
    }
    //清除缓存
    String[] temp = uuids.split(",");
    Cache.deleteFlowDataCache(temp[temp.length - 1] + "model");
    Cache.deleteFlowDataCache(temp[temp.length - 1]);
    return flowOrderServiceI.save(flowOrder);
  }

  /**
   * 同意审批流程
   */
  @Override
  public String consentFlowById(String flowId, String memo, String formDatas) {
    return consentFlowById(flowId, memo, SecurityUtils.currentUsername(),
        SecurityUtils.currentUserInfo().getCname(), false, null, formDatas);
  }

  /**
   * 移动端同意审批流程
   */
  @Override
  @Transactional
  public String consentFlowById(String flowId, String memo, String currentUserId,
                                String currentUserName, Boolean fromApp, Task nowTask, String formDatas) {
    String consentMemo = memo;
    log.info("同意操作  flowId:{},currentUserId:{},memo:{}", flowId, currentUserId, memo);
    //检查是否有审批权限
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    //非直接通过下一节点
    if (nowTask == null) {
      checkApprove(flowOrder, currentUserId);
    }
    String procInstId = flowOrder.getProcInstId();
    FormData formData = formDataServiceI.findByProcInstId(procInstId);
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    //查询被代理人
    String agentUser = getAgentUserName(flowOrder.getNextApproveUsers(), currentUserId, flowOrder.getApplyTypeKey());
    //获取当前审批人当前流程的task节点
    if (nowTask == null) {
      nowTask = getTaskByProcInstIdAndapproveUser(procInstId, currentUserId, flowModel.getFormKey());
    }
    //判断是否需要重置分支条件  修改formDatas
    chengeFlowCondition(nowTask, flowModel, procInstId, formDatas, flowOrder, currentUserId);

    String taskId = nowTask.getId();
    String nodeDefKey = nowTask.getTaskDefinitionKey();
    //当前节点审批人
    String approveUsers = activitiServiceI.getApproveUser(taskId);

    //当前审批节点的下一节点审批人(代办通知人)
    String nestApproveNotifyUsers;
    //下一节点审批人（所有节点）
    String nestApproveUsers;
    //同意流程
    if (nowTask.getDelegationState() != null && Constant.FLOW_COUNTERSIGN_ING
        .equalsIgnoreCase(nowTask.getDelegationState().name())) {
      log.info("加签审批同意");
      //加签状态 同意审批
      taskService.resolveTask(taskId);
      //获取加签过节点审批同意 下一审批人（加签操作人）
      nestApproveNotifyUsers = activitiServiceI.getApproveUser(taskId);
      //获取审批前（去掉加签节点）所有节点审批人 + 加签操作人
      List<Task> taskList = new ArrayList<>();
      taskList.add(nowTask);
      nestApproveUsers = activitiServiceI.getApproveNestUsers(procInstId, taskList) + nestApproveNotifyUsers;
    } else {
      //获取当前流程的所有task节点
      List<Task> tasks = activitiServiceI.getTaskByProcInstId(procInstId);
      //正常状态 同意审批
      activitiServiceI.completeByTaskId(taskId);
      //去掉审批前所有节点  获取审批后新节点审批人
      nestApproveNotifyUsers = activitiServiceI.getApproveNestUsers(procInstId, tasks);
      //获取所有节点审批人
      nestApproveUsers = activitiServiceI.getApproveNestUsers(procInstId, null);
    }
    log.info("当前流程所有节点审批人:nestApproveUsers:{}", nestApproveUsers);
    log.info("当前审批节点下一节点审批人:nestApproveNotifyUsers:{}", nestApproveNotifyUsers);

    //创建日志并保存
    ApproveLog approveLog = approveLogServiceI.createApproveLog(procInstId, taskId, nodeDefKey, nowTask.getName(), flowOrder.getApplyTypeName(),
        Constant.FLOW_CONSENT, addMemoPrefix(memo, agentUser, currentUserId, fromApp, Constant.FLOW_CONSENT),
        currentUserId, currentUserName);
    //判断是否需要修改formDatas
    if (isChangeFormData(nowTask, flowModel, formDatas, flowOrder)) {
      formData.setFormDatas(formDatas);
    }
    //存储当前节点流程表单数据
    flowNodeDataServiceI.saveFlowNodeData(currentUserId, currentUserName, formData.getFormDatas(), flowModel.getFormKey(),
        flowModel.getFormName(), nodeDefKey, approveLog.getTaskName(), procInstId);
    //获取知会人  删除缓存
    String notifyUsers = getNotifyUsers(procInstId);

    //修改flowOrder
    long taskNum = activitiServiceI.getTaskCountByProcInstId(flowOrder.getProcInstId());
    flowOrderServiceI.approveUpdateFlowOrder(flowOrder, nestApproveUsers, nestApproveNotifyUsers,
        currentUserId, agentUser, notifyUsers, taskNum, Constant.FLOW_CONSENT);

    //下一节点是否可以直接通过
    if (passNestNode(procInstId, flowOrder.getApplyStatus(), flowModel, flowId, consentMemo,
        currentUserId, currentUserName, fromApp)) {
      return "审批通过";
    }
    try {
      //发送通知
      sendNotify(flowOrder, notifyUsers, agentUser, currentUserName, currentUserId, taskNum, nestApproveNotifyUsers,
          Constant.FLOW_CONSENT, approveUsers);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      //推送移动端已办
      pushHistory(flowOrder, "1", currentUserId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "审批通过";
  }

  /**
   * 判断节点是否可以直接跳过
   */
  private boolean passNestNode(String procInstId, Integer status, FlowModel flowModel, String flowId,
                               String consentMemo,
                               String currentUserId, String currentUserName, Boolean fromApp) {

    //获取当前流程的所有task节点
    List<Task> tasks = activitiServiceI.getTaskByProcInstId(procInstId);
    if (status != 0) {
      if (StringUtils.isNotBlank(flowModel.getNodeProperty())) {
        Map<String, Object> nodeProperty = CommonUtil.s2m(flowModel.getNodeProperty());
        for (Task task : tasks) {
          if (nodeProperty.keySet().contains(task.getName())) {
            if (Constant.FLOW_MODEL_NODE_PROPERTY_NOT_PASS.equals(nodeProperty.get(task.getName()))) {
              return false;
            }
          }
          //判断当前节点是否禁止跳过
          String approveUsers = activitiServiceI.getApproveUser(task.getId());
          if (",".equals(approveUsers)) {
            log.info("当前审批的下一节点无审批人，直接通过！");
            //下一节点无审批人  直接审批同意
            consentFlowById(flowId, Constant.FLOW_NO_APPROVE_USERS + consentMemo, currentUserId, currentUserName,
                fromApp, task, "");
            return true;
          } else if (approveUsers.contains("," + currentUserId + ",")) {
            log.info("当前审批的下一节点审批人包含自己，直接通过！");
            //下一节点审批人包含自己  审批同意
            consentFlowById(flowId, Constant.FLOW_APPROVE_USERS_REP + consentMemo, currentUserId, currentUserName,
                fromApp, task, "");
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * 修改表单和分支条件
   */
  private void chengeFlowCondition(Task task, FlowModel flowModel, String procInstId, String formDatas, FlowOrder flowOrder, String currentUserId) {
    if (StringUtils.isNotBlank(formDatas)) {
      //节点可编辑
      if (StringUtils.isNotBlank(flowModel.getEditNode())) {
        Map editNode = CommonUtil.s2m(flowModel.getEditNode());
        if (editNode.keySet().contains(task.getName()) || (flowOrder.getApplyUserId().contains(String.valueOf(task.getAssignee())) && Constant.FLOW_COUNTERSIGN_ING.equalsIgnoreCase(task.getDelegationState().name()) && editNode.keySet().contains("申请人"))) {
          //修改数据表单
          formDataServiceI.updateFromData(procInstId, formDatas);
          List<Map<String, String>> editColumn = (List) editNode.get(task.getName());
          if (editColumn == null && (flowOrder.getApplyUserId().contains(String.valueOf(task.getAssignee())) && Constant.FLOW_COUNTERSIGN_ING.equalsIgnoreCase(task.getDelegationState().name()))) {
            editColumn = (List) editNode.get("申请人");
          }
          for (Map<String, String> map : editColumn) {
            //节点可编辑有必填字段  修改分支条件
            if (Constant.FLOW_MODEL_EDIT_NODE_REQUIRED.equals(map.get("status"))) {
              //获取节点分支条件数据存入流程
              String[] Variables = getKeyValue(flowModel, CommonUtil.s2m(formDatas), Constant.FLOW_ORDER_APPROVE, currentUserId);
              log.info("Variables:{}", Variables);
              activitiServiceI.changeVariablesByExecutionId(task.getExecutionId(), Variables[0], Variables[1], currentUserId);
            }
          }
        }
      }
    }
  }

  /**
   * 判断是否修改表单数据
   */
  private boolean isChangeFormData(Task task, FlowModel flowModel, String formDatas, FlowOrder flowOrder) {
    if (StringUtils.isNotBlank(formDatas)) {
      //节点可编辑
      if (StringUtils.isNotBlank(flowModel.getEditNode())) {
        Map editNode = CommonUtil.s2m(flowModel.getEditNode());
        if (editNode.keySet().contains(task.getName()) || (flowOrder.getApplyUserId().contains(String.valueOf(task.getAssignee())) && Constant.FLOW_COUNTERSIGN_ING.equalsIgnoreCase(task.getDelegationState().name()) && editNode.keySet().contains("申请人"))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 转交审批流程
   *
   * @param flowId 流程实例id
   * @param userId 被转交人qtalk
   * @param memo   审批意见
   */
  @Override
  public String forwardFlowById(String flowId, String userId, String memo, String formDatas) {
    return forwardFlowById(flowId, userId, memo, SecurityUtils.currentUsername(),
        SecurityUtils.currentUserInfo().getCname(), false, formDatas);
  }

  /**
   * 转交审批流程  移动端
   *
   * @param flowId 流程实例id
   * @param userId 被转交人qtalk
   * @param memo   审批意见
   */
  @Override
  @Transactional
  public String forwardFlowById(String flowId, String userId, String memo, String currentUserId,
                                String currentUsername, Boolean fromApp, String formDatas) {
    log.info("转交 flowId:{},userId:{},currentUserId:{}", flowId, userId, currentUserId);
    //是否有审批权限
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    checkApprove(flowOrder, currentUserId);
    if (StringUtils.isBlank(userId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_FORWARD_USERID_NOT_NULL);
    }
    /*//不可以转交给自己
    if (userId.equals(currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_COUNT_FORWARD_TO_SELF);
    }*/
    //获取当前流程实例flowOrder及当前task任务
    String procInstId = flowOrder.getProcInstId();
    //根据流程实例和当前登录人获取待办任务
    Task nowTask = getTaskByProcInstIdAndapproveUser(procInstId, currentUserId, flowOrder.getApplyTypeKey());
    //加签之后不允许转交
    if (nowTask.getDelegationState() != null && Constant.FLOW_COUNTERSIGN_ING
        .equalsIgnoreCase(nowTask.getDelegationState().name())) {
      throw new FlowException(ResultEnum.FLOW_COUNT_FORWARD);
    }
    //转交给自己 activiti审批人不做处理
    if (!userId.equals(currentUserId)) {
      //将当前任务节点待审批人替换为转交人
      taskService.addCandidateUser(nowTask.getId(), userId);
      //删除该节点当前人的审批权限
      activitiServiceI.deleteApproveUsers(nowTask.getId(), currentUserId, 0);
    }

    //1 修改formData
    FormData formData = formDataServiceI.findByProcInstId(procInstId);
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    //判断是否修改表单数据
    if (isChangeFormData(nowTask, flowModel, formDatas, flowOrder)) {
      formData = formDataServiceI.updateFromData(procInstId, formDatas);
    }

    //2 approveLog
    //代理人
    String agentUser = getAgentUserName(flowOrder.getNextApproveUsers(), currentUserId, flowOrder.getApplyTypeKey());
    //创建日志并保存
    ApproveLog approveLog = approveLogServiceI.createApproveLog(procInstId, nowTask.getId(), nowTask.getTaskDefinitionKey(), nowTask.getName(),
        flowOrder.getApplyTypeName(), Constant.FLOW_FORWARD,
        addMemoPrefix(memo, agentUser, currentUserId, fromApp, Constant.FLOW_FORWARD), currentUserId, currentUsername);
    // 3 flowNodeData
    //存储当前节点流程表单数据
    flowNodeDataServiceI.saveFlowNodeData(currentUserId, currentUsername, formData.getFormDatas(), flowModel.getFormKey(),
        flowModel.getFormName(), nowTask.getTaskDefinitionKey(), approveLog.getTaskName(), procInstId);

    //4 flowOrder
    //下一节点审批人（所有节点）
    List<Task> taskList = new ArrayList<>();
    taskList.add(nowTask);
    String nestApproveUsers = activitiServiceI.getApproveNestUsers(procInstId, taskList) + userId + ",";
    //修改flowOrder
    flowOrderServiceI.approveUpdateFlowOrder(flowOrder, nestApproveUsers, userId, currentUserId, agentUser, "",
        1, Constant.FLOW_FORWARD);
    try {
      //发通知
      sendNotify(flowOrder, "", agentUser, currentUsername, currentUserId, 1, userId,
          Constant.FLOW_FORWARD, "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      //推送移动端已办
      pushHistory(flowOrder, "1", currentUserId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "审批已转交给" + userId;


  }

  /**
   * 加签审批流程
   *
   * @param flowId 流程实例id
   * @param userId 加签人qtalk
   * @param memo   审批意见
   */
  @Override
  @Transactional
  public String counterSignFlowById(String flowId, String userId, String memo, String formDatas) {
    return counterSignFlowById(flowId, userId, memo, SecurityUtils.currentUsername(),
        SecurityUtils.currentUserInfo().getCname(), false, formDatas);
  }

  /**
   * 加签审批流程
   *
   * @param flowId 流程实例id
   * @param userId 加签人qtalk
   * @param memo   审批意见
   */
  @Override
  @Transactional
  public String counterSignFlowById(String flowId, String userId, String memo, String currentUserId,
                                    String currentUsername, Boolean fromApp, String formDatas) {
    log.info("加签 flowId:{},userId:{},currentUserId:{}", flowId, userId, currentUserId);
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    //是否有审批权限
    checkApprove(flowOrder, currentUserId);
    if (StringUtils.isBlank(userId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_COUNTERSIGN_USERID_NOT_NULL);
    }
    //不可以加签给自己
    if (userId.equals(currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_COUNT_COUNTER_TO_SELF);
    }
    String procInstId = flowOrder.getProcInstId();
    //根据流程实例和当前登录人获取待办任务
    Task nowTask = getTaskByProcInstIdAndapproveUser(procInstId, currentUserId, flowOrder.getApplyTypeKey());
    //加签之后不允许加签
    if (nowTask.getDelegationState() != null && Constant.FLOW_COUNTERSIGN_ING
        .equalsIgnoreCase(nowTask.getDelegationState().name())) {
      throw new FlowException(ResultEnum.FLOW_COUNT_COUNTER);
    }
    //加签流程
    taskService.delegateTask(nowTask.getId(), userId);
    //删除除userId外全部审批人
    activitiServiceI.deleteApproveUsers(nowTask.getId(), userId, 1);

    String nestApproveUsers = activitiServiceI.getApproveNestUsers(procInstId, null);

    //添加当前人  用于加签回归后 返回给当前人
    taskService.addCandidateUser(nowTask.getId(), currentUserId);

    FormData formData = formDataServiceI.findByProcInstId(procInstId);
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    //判断是否修改表单数据
    if (isChangeFormData(nowTask, flowModel, formDatas, flowOrder)) {
      formData = formDataServiceI.updateFromData(procInstId, formDatas);
    }

    //代理人
    String agentUser = getAgentUserName(flowOrder.getNextApproveUsers(), currentUserId, flowOrder.getApplyTypeKey());
    //创建日志并保存
    ApproveLog approveLog = approveLogServiceI.createApproveLog(procInstId, nowTask.getId(), nowTask.getTaskDefinitionKey(), nowTask.getName(),
        flowOrder.getApplyTypeName(), Constant.FLOW_COUNTERSIGN,
        addMemoPrefix(memo, agentUser, currentUserId, fromApp, Constant.FLOW_COUNTERSIGN), currentUserId, currentUsername);

    //存储当前节点流程表单数据
    flowNodeDataServiceI.saveFlowNodeData(currentUserId, currentUsername, formData.getFormDatas(), flowModel.getFormKey(),
        flowModel.getFormName(), nowTask.getTaskDefinitionKey(), approveLog.getTaskName(), procInstId);

    //修改flowOrder
    flowOrderServiceI.approveUpdateFlowOrder(flowOrder, nestApproveUsers, userId, currentUserId, agentUser, "",
        1, Constant.FLOW_COUNTERSIGN);

    try {
      //发通知
      sendNotify(flowOrder, "", agentUser, currentUsername, currentUserId, 1, userId,
          Constant.FLOW_COUNTERSIGN, "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      //推送移动端已办
      pushHistory(flowOrder, "1", currentUserId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "审批加签交给" + userId;
  }

  /**
   * 根据procInstId（activiti流程id） 和 approveUser（待审批人） 查询task
   */
  private Task getTaskByProcInstIdAndapproveUser(String procInstId, String approveUser, String formKey) {
    Task task = activitiServiceI.getTaskByProcInstIdAndUser(procInstId, approveUser);
    if (task != null) {
      return task;
    } else {
      // 判断代理人的流程  获取被代理人
      Optional<List<Agent>> optionalAgents = agentServiceI.getAgentByAgent(approveUser);
      if (optionalAgents.isPresent()) {
        for (Agent agent : optionalAgents.get()) {
          if ("all".equalsIgnoreCase(agent.getProcessID()) || ("," +agent.getProcessID()+ ",").contains("," + formKey + ",")) {
            task = activitiServiceI.getTaskByProcInstIdAndUser(procInstId, agent.getQtalk());
          }
          if (task != null) {
            return task;
          }
        }
      }
    }
    //未获取到task  该节点已经被审批
    log.info("获取task失败 procInstId:{},approveUser:{}", procInstId, approveUser);
    throw new FlowException(ResultEnum.FLOW_ORDER_APPROVED);
  }

  /**
   * 拒绝审批流程
   */
  @Override
  public String rejectFlowById(String flowId, String memo, String formDatas) {
    return rejectFlowById(flowId, memo, SecurityUtils.currentUsername(),
        SecurityUtils.currentUserInfo().getCname(), false, formDatas);
  }

  /**
   * 拒绝审批流程
   */
  @Override
  @Transactional
  public String rejectFlowById(String flowId, String memo, String currentUserId,
                               String currentUsername, Boolean fromApp, String formDatas) {
    log.info("拒绝 flowId:{},currentUserId:{},memo:{}", flowId, currentUserId, memo);
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    //是否有审批权限
    checkApprove(flowOrder, currentUserId);
    String procInstId = flowOrder.getProcInstId();
    Task nowTask = getTaskByProcInstIdAndapproveUser(procInstId, currentUserId, flowOrder.getApplyTypeKey());
    //拒绝流程
    activitiServiceI.rejectTask(nowTask.getId(), memo);

    FormData formData = formDataServiceI.findByProcInstId(procInstId);
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    //判断是否修改表单数据
    if (isChangeFormData(nowTask, flowModel, formDatas, flowOrder)) {
      formData = formDataServiceI.updateFromData(procInstId, formDatas);
    }
    //代理人
    String agentUser = getAgentUserName(flowOrder.getNextApproveUsers(), currentUserId, flowOrder.getApplyTypeKey());

    //创建日志并保存
    ApproveLog approveLog = approveLogServiceI.createApproveLog(procInstId, nowTask.getId(), nowTask.getTaskDefinitionKey(), nowTask.getName(),
        flowOrder.getApplyTypeName(), Constant.FLOW_REJECT,
        addMemoPrefix(memo, agentUser, currentUserId, fromApp, Constant.FLOW_REJECT), currentUserId, currentUsername);

    //存储当前节点流程表单数据
    flowNodeDataServiceI.saveFlowNodeData(currentUserId, currentUsername, formData.getFormDatas(), flowModel.getFormKey(),
        flowModel.getFormName(), nowTask.getTaskDefinitionKey(), approveLog.getTaskName(), procInstId);

    //修改flowOrder
    flowOrderServiceI.approveUpdateFlowOrder(flowOrder, "", "", currentUserId, agentUser, "", 0, Constant.FLOW_REJECT);

    try {
      //发通知
      sendNotify(flowOrder, "", agentUser, currentUsername, currentUserId, 0, "",
          Constant.FLOW_REJECT, "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      //推送移动端已办
      pushHistory(flowOrder, "1", currentUserId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "审批拒绝";
  }

  /**
   * 判断当前登录用户是否有审批当前单子的权限
   */
  private void checkApprove(FlowOrder flowOrder, String currentUserId) {
    String nestApproveUsers = flowOrder.getNextApproveUsers();
    log.info("nestApproveUsers:{}", nestApproveUsers);
    if (!nestApproveUsers.contains("," + currentUserId + ",") &&
        !isInAgent(nestApproveUsers, flowOrder.getApplyTypeKey(), currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_NOT_APPROVE);
    }
  }

  /**
   * 判断当前操作人是否在下一节点审批人列表的代理人中
   */
  private boolean isInAgent(String nestApproveUsers, String flowModelKey, String currentUserId) {
    Optional<List<Agent>> agents = agentServiceI.getAgentByAgent(currentUserId);
    if (!agents.isPresent()) {
      return false;
    } else {
      for (Agent agent : agents.get()) {
        if (nestApproveUsers.contains("," + agent.getQtalk() + ",") && ("all".equalsIgnoreCase(agent.getProcessID())
            || ("," +agent.getProcessID()+ ",").contains("," + flowModelKey + ","))) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * 查询下一节点审批人中的被代理人
   */
  private String getAgentUserName(String nextApproveUsers, String currentUserId, String flowOrderKey) {
    //当前审批人是否在审批人列表中
    if (!nextApproveUsers.contains("," + currentUserId + ",")) {
      Optional<List<Agent>> agents = agentServiceI.getAgentByAgent(currentUserId);
      for (Agent agent : agents.get()) {
        String qtalk = agent.getQtalk();
        String processID = agent.getProcessID();
        if (StringUtils.isNotBlank(qtalk) && nextApproveUsers.contains("," + qtalk + ",") && (
            "all".equalsIgnoreCase(processID) || ("," + processID + ",").contains("," + flowOrderKey + ","))) {
          return qtalk;
        }
      }
    }
    return "";
  }

  /**
   * 添加审批意见前缀
   */
  private String addMemoPrefix(String memo, String agentUser, String currentUserId, boolean fromApp, String type) {
    memo = memo == null || "null".equalsIgnoreCase(memo) ? "" : memo;
    if (Constant.FLOW_CONSENT.equals(type)) {
      //同意
      memo = Constant.FLOW_CONSENT_Z + memo;
    } else if (Constant.FLOW_FORWARD.equals(type)) {
      //转交
      memo = Constant.FLOW_FORWARD_Z + memo;
    } else if (Constant.FLOW_COUNTERSIGN.equals(type)) {
      //加签
      memo = Constant.FLOW_COUNTERSIGN_Z + memo;
    } else if (Constant.FLOW_REJECT.equals(type)) {
      //拒绝
      memo = Constant.FLOW_REJECT_Z + memo;
    } else if (Constant.FLOW_START.equals(type)) {
      //发起
      memo = Constant.FLOW_START_Z + memo;
    } else if (Constant.FLOW_REVOKE.equals(type)) {
      memo = Constant.FLOW_REVOKE_Z + memo;
    }
    //是否是代理人
    memo += StringUtils.isBlank(agentUser) ? "" : "(" + currentUserId + "代理" + agentUser + "处理)";
    memo = fromApp ? "[来自移动端审批]" : "" + memo;
    return memo;
  }

  //获取知会人
  public String getNotifyUsers(String procInstId) {
    //获取知会人  删除缓存
    String notifyUsers = String.valueOf(Cache.getFlowDataCacheByKey(procInstId));
    notifyUsers = StringUtils.isNotBlank(notifyUsers) && !"null".equalsIgnoreCase(notifyUsers) ? notifyUsers : "";
    Cache.deleteFlowDataCache(procInstId);
    log.info("知会人:{}", notifyUsers);
    return notifyUsers;
  }

  /**
   * 发送通知
   */
  public void sendNotify(FlowOrder flowOrder, String notifyUsers, String agentUser, String currentUserName,
                         String currentUserId, long taskNum, String nestApproveNotifyUsers, String type, String nowNodeApproveUsers) {
    if (StringUtils.isNotBlank(notifyUsers)) {
      //发送知会通知
      notifyServiceI.sendNotify(new Notify(flowOrder.getApplyUserName(), flowOrder.getApplyUserId(), "发起了",
          flowOrder.getApplyTypeName(), 2, flowOrder.getId()), notifyUsers);
    }
    //发送通知给发起人
    String agentMsg = (StringUtils.isBlank(agentUser) ? "" : "（代理" + agentUser + ")");
    if (Constant.FLOW_START.equals(type)) {
      //发起
      notifyServiceI.sendNotify(new Notify(currentUserName, currentUserId, "发起了",
              flowOrder.getApplyTypeName(), 1, flowOrder.getId())
          , flowOrder.getApplyUserId());
    } else if (Constant.FLOW_FORWARD.equals(type)) {
      //转交
      notifyServiceI.sendNotify(
          new Notify(currentUserName + agentMsg, currentUserId, "转交了你的",
              flowOrder.getApplyTypeName(), 1, flowOrder.getId()), flowOrder.getApplyUserId());
    } else if (Constant.FLOW_COUNTERSIGN.equals(type)) {
      //加签
      notifyServiceI.sendNotify(new Notify(
              currentUserName + agentMsg, currentUserId, "将你的",
              flowOrder.getApplyTypeName() + "加签给" + nestApproveNotifyUsers,
              1, flowOrder.getId()),
          flowOrder.getApplyUserId());
    } else if (Constant.FLOW_REJECT.equals(type)) {
      //拒绝
      notifyServiceI.sendNotify(
          new Notify(currentUserName + agentMsg, currentUserId, "拒绝了你的",
              flowOrder.getApplyTypeName(), 1, flowOrder.getId()), flowOrder.getApplyUserId());
    } else {
      //同意
      notifyServiceI.sendNotify(new Notify(currentUserName + agentMsg + (taskNum == 0 ? "(最后审批人)" : ""),
              currentUserId, "同意了你的", flowOrder.getApplyTypeName(), 1, flowOrder.getId())
          , flowOrder.getApplyUserId());
      //发送通知给当前节点其他审批人和代理人  已审批
      if (StringUtils.isNotBlank(nowNodeApproveUsers)) {
        String otherApproveUsers = nowNodeApproveUsers.replaceAll("," + currentUserId + ",", ",");
        if (StringUtils.isNotBlank(otherApproveUsers)) {
          notifyServiceI.sendNotify(new Notify(currentUserName + agentMsg + (taskNum == 0 ? "(最后审批人)" : ""),
                  currentUserId, "同意了" + flowOrder.getApplyUserName() + "的", flowOrder.getApplyTypeName(), 4,
                  flowOrder.getId())
              , getAgentUsers(otherApproveUsers, flowOrder.getApplyTypeKey()));
        }
      }
    }
    //发送通知给下一节点审批人
    if (taskNum != 0) {
      //同时 发送通知给代理人
      notifyServiceI.sendNotify(
          new Notify(flowOrder.getApplyUserName(), flowOrder.getApplyUserId(), "发起了",
              flowOrder.getApplyTypeName(), 0,
              flowOrder.getId()), getAgentUsers(nestApproveNotifyUsers, flowOrder.getApplyTypeKey()));
    }
  }

  /**
   * 添加代理人到下一节点审批人中  用来发送通知
   */
  private String getAgentUsers(String nestUser, String flowModelKey) {
    StringBuffer users = new StringBuffer();
    Arrays.stream(nestUser.split(",")).forEach(user -> {
      if (StringUtils.isNotBlank(user)) {
        users.append(",").append(user).append(",");
        List<Agent> agentList = agentServiceI.getAgentByQtalk(user);
        if(agentList != null) {
          agentList.forEach(agent -> {
            if (agent != null && ("all".equalsIgnoreCase(agent.getProcessID()) || ("," + agent.getProcessID() + ",")
                .contains("," + flowModelKey + ","))) {
              users.append(agent.getAgent()).append(",");
            }
          });
        }
      }
    });
    return users.toString();
  }

  @Override
  public Optional<List<FlowOrder>> getTodos(int queryType) {
    return flowOrderServiceI.findByApplyStatus(queryType);
  }

  @Override
  public Boolean inExitNode(FlowOrder flowOrder) {
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKey(flowOrder.getApplyTypeKey());
    if (flowModel != null && StringUtils.isNotBlank(flowModel.getEditNode())) {
      Map<String, Object> editNode = CommonUtil.s2m(flowModel.getEditNode());
      for (Task task : taskService.createTaskQuery().processInstanceId(flowOrder.getProcInstId()).list()) {
        if (editNode.keySet().contains(task.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 向移动端推送历史
   */
  private void pushHistory(FlowOrder flowOrder, String type, String currentUser) {

    Map<String, Object> opsappHistoryParam = new HashMap<>();

    List data = new ArrayList<>();

    Map<String, Object> opsappHistoryDataMap = new HashMap<>();

    List dataSource = new ArrayList<>();
    dataSource.add(new OpsappHistoryData(flowOrder, type, currentUser));

    opsappHistoryDataMap.put("processKeys", "QUNAR_SUPER_OA");
    opsappHistoryDataMap.put("dataSource", dataSource);

    data.add(opsappHistoryDataMap);

    opsappHistoryParam.put("data", data);

    Object resultJson = opsappApiServiceI.history(opsappHistoryParam);
    log.info(resultJson.toString());
    Map result = CommonUtil.o2m(resultJson);

    if ("0.0".equalsIgnoreCase(result.get("errcode").toString())) {
      log.info("推送已办成功");
    } else {
      log.error("推送已办失败：{}", result);
    }
  }

  /**
   * 催办
   */
  @Override
  @Transactional
  public String notifyNextApproveUsers(String flowId) throws Exception {
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    //只能发起人催办
    String currentUserId = SecurityUtils.currentUsername();
    if (!flowOrder.getApplyUserId().equalsIgnoreCase(currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_NOT_NOTIFY_APPROVE);
    }
    flowOrder
        .setNextApproveUsers(getAgentUsers(flowOrder.getNextApproveUsers(), flowOrder.getApplyTypeKey()));
    String qtalkContent = getMailAndQtalkContent(flowOrder).get("qtalk");
    String mailContent = getMailAndQtalkContent(flowOrder).get("mail");

    //发送qtalk消息
    OpsappSendMessageInfo opsappSendMessageInfo = new OpsappSendMessageInfo(flowOrder, "[催办]");
    opsappSendMessageInfo.setContent(qtalkContent);
    opsappSendMessageInfo.setBody(qtalkContent);
    opsappSendMessageInfo.setReacturl(qtalkoaApi + "@oid:" + flowOrder.getId());
    opsappSendMessageInfo.setLinkurl(qunaroaApi + "/cooperate/detail/" + flowOrder.getId());
    Object qtalkStatus;
    //若配置文件显示不发送qtalk，则直接返回发送成功
    if ("false".equalsIgnoreCase(isSendQtalk)) {
      qtalkStatus = new JsonObject();
      ((JsonObject) qtalkStatus).addProperty("errcode", "0");
    } else {
      remindApproveUsersRunnable.startQtalkRemindRunnable(opsappSendMessageInfo);
    }

    //发送email消息
    MailInfo mailInfo = new MailInfo(flowOrder, "[催办]");
    mailInfo.setContent(mailContent);
    mailInfo.setLinkUrl(qunaroaApi + "/cooperate/detail/" + flowOrder.getId());
    //生成邮件中qtalk扫一扫二维码图片
    String qrCodeUrl = getQRCodeUrl(flowOrder);
    mailInfo.setQrCodeUrl(qrCodeUrl);

    remindApproveUsersRunnable.startMailRemindRunnable(mailInfo);

    return "催办成功";
  }

  /**
   * 根据表单生成二维码并返回url
   */
  public String getQRCodeUrl(FlowOrder flowOrder) throws Exception {
    ByteArrayOutputStream outputStream = QRCodeUtil.toOutputStreamWithLogo(
        qtalkoaApi + "@oid:" + flowOrder.getId(), Constant.QR_QUNAR_LOGO, 300, 300);
    String qrCodeUrl = null;
    try {
      qrCodeUrl = minioUtils.uploadImageToMinio(new ByteArrayInputStream(outputStream.toByteArray()), flowOrder.getId()).getUrl();
    } catch (IOException e) {
      log.error("将二维码图片上传到ceph服务器失败", e);
    }
    return qrCodeUrl;
  }

  /**
   * 根据FlowOrder获取mail和qtalk消息内容
   */
  public Map<String, String> getMailAndQtalkContent(FlowOrder flowOrder) {
    Map<String, String> notifyMap = Maps.newHashMap();
    FormData formData = formDataServiceI.findByProcInstId(flowOrder.getProcInstId());
    FlowModelDto flowModelDto = flowModelServiceI
        .getFlowModelDtoByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    List<String> keys = JsonMapUtil.getKeysByKey(flowModelDto.getFormModelJson());
    StringBuilder qtalkContent = new StringBuilder();
    StringBuilder mailContent = new StringBuilder();
    if (keys != null && keys.size() > 0) {
      qtalkContent.append("摘要:\r\n");
      for (String key : keys) {
        qtalkContent.append(key).append(" : ");
        mailContent.append(key).append(":");
        qtalkContent.append(StringUtils.isNotBlank(
            JsonMapUtil.getValueByKey(key, CommonUtil.s2m(formData.getFormDatas())))
            ? JsonMapUtil.getValueByKey(key, CommonUtil.s2m(formData.getFormDatas())) : "暂无详细信息")
            .append(";\r\n");
        mailContent.append(JsonMapUtil.getValueByKey(key, CommonUtil.s2m(formData.getFormDatas())))
            .append(";");
      }
    } else {
      qtalkContent.append("摘要:\r\n").append(flowOrder.getHeadline());
      mailContent.append("摘要:\r\n").append(flowOrder.getHeadline());
    }
    notifyMap.put("mail", mailContent.toString());
    notifyMap.put("qtalk", qtalkContent.toString());

    return notifyMap;
  }

  /**
   * 获取当前流程节点流程跟踪图
   */
  @Override
  @Transactional
  public String getFlowTraceImage(String flowId) throws Exception {
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    String processInstanceId = flowOrder.getProcInstId();
    InputStream imageStream;
    //待审批流程 --- 流程未结束
    if (flowOrder.getApplyStatus() == 1) {
      //当前任务节点
      List<Task> taskList = taskService.createTaskQuery().active()
          .processInstanceId(processInstanceId).list();
      //需高亮节点
      List<String> activeActivitiIds = Lists.newArrayList();
      taskList.forEach(task -> activeActivitiIds.add(task.getTaskDefinitionKey()));

      BpmnModel bpmnModel = repositoryService
          .getBpmnModel(taskList.get(0).getProcessDefinitionId());

      imageStream = processDiagramGenerator
          .generateDiagram(bpmnModel, activeActivitiIds,
              Collections.emptyList(), "simsun", "simsun", "simsun");
    } else { //流程已结束，成为历史流程

      //查询历史流程
      HistoricProcessInstance historicProcessInstance = historyService
          .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
      //根据历史流程定义id获取流程类
      BpmnModel bpmnModel = repositoryService
          .getBpmnModel(historicProcessInstance.getProcessDefinitionId());
      //获取历史流程相关的历史任务
      List<HistoricActivityInstance> highLightedActivitList = historyService
          .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
      //高亮节点id集合
      List<String> highLightedActivities = Lists.newArrayList();
      highLightedActivitList.forEach(
          highLightedActiviti -> highLightedActivities.add(highLightedActiviti.getActivityId()));
      //高亮线路id集合
      List<String> highLightedFlows = getHighLightedFlows(bpmnModel, highLightedActivitList);

      imageStream = processDiagramGenerator
          .generateDiagram(bpmnModel, highLightedActivities, highLightedFlows, "simsun",
              "simsun", "simsun");
    }

    UserAttachment userAttachment = minioUtils.uploadTraceImageForUrl(imageStream, flowOrder.getHeadline());

    return userAttachment.getUrl();
  }


  /**
   * 获取流程图高亮线路ID集合
   *
   * @param bpmnModel                 流程bpmn
   * @param historicActivityInstances 历史任务实例
   */
  private List<String> getHighLightedFlows(BpmnModel bpmnModel,
                                           List<HistoricActivityInstance> historicActivityInstances) {
    //24小时制
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //用以保存高亮的线flowId
    List<String> highFlows = new ArrayList<>();
    // 对历史流程节点进行遍历
    for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
      //得到节点定义的详细信息
      FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess()
          .getFlowElement(historicActivityInstances.get(i).getActivityId());
      //用以保存后开始时间相同的节点
      List<FlowNode> sameStartTimeNodes = new ArrayList<>();
      FlowNode sameActivityImpl1 = null;
      // 第一个节点
      HistoricActivityInstance activityImplTemp1 = historicActivityInstances.get(i);
      HistoricActivityInstance activityImpTemp2;
      for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
        //后续第1个节点
        activityImpTemp2 = historicActivityInstances.get(k);
        if ("userTask".equals(activityImplTemp1.getActivityType()) && "userTask"
            .equals(activityImpTemp2.getActivityType()) &&
            //都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
            df.format(activityImplTemp1.getStartTime()).equals(df.format(activityImpTemp2.getStartTime()))) {
        } else {
          //找到紧跟在后面的一个节点
          sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess()
              .getFlowElement(historicActivityInstances.get(k).getActivityId());
          break;
        }

      }
      //将后面第一个节点放在时间相同节点的集合里
      sameStartTimeNodes.add(sameActivityImpl1);
      for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
        //后续第一个节点
        HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
        //后续第二个节点
        HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
        //如果第一个节点和第二个节点开始时间相同保存
        if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {
          FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess()
              .getFlowElement(activityImpl2.getActivityId());
          sameStartTimeNodes.add(sameActivityImpl2);
        } else {// 有不相同跳出循环
          break;
        }
      }
      //取出节点的所有出去的线
      List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows();
      //对所有的线进行遍历
      for (SequenceFlow pvmTransition : pvmTransitions) {
        //如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
        FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(pvmTransition.getTargetRef());
        if (sameStartTimeNodes.contains(pvmActivityImpl)) {
          highFlows.add(pvmTransition.getId());
        }
      }
    }
    return highFlows;

  }

  /**
   * app 获取详情
   */
  @Override
  @Transactional
  public List<Map<String, String>> getFlowByIdFromApp(String flowId) {
    List<Map<String, String>> dataList = new ArrayList<>();
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    FormData formData = formDataServiceI.findByProcInstId(flowOrder.getProcInstId());
    JsonMapUtil.getAllKeys(CommonUtil.s2m(formData.getFormDatas())).forEach((k, v) -> {
      Map<String, String> dataMap = Maps.newHashMap();
      dataMap.put("k", k);
      dataMap.put("v", v);
      dataList.add(dataMap);
    });

    return dataList;
  }

  /**
   * 替换任务节点待审批人
   *
   * @param flowId  流程表单ID
   * @param userId  被替换审批人
   * @param userIds 替换的审批人，可以多个，用英文逗号隔开
   */
  @Override
  @Transactional
  public String updateNodeApproveUsers(String flowId, String userId, String userIds, String memo, String operate) {

    //校验权限
    if (SecurityUtils.currentUserInfo().getCurrentAuthority().size() < 2) {
      throw new FlowException(ResultEnum.FLOW_ORDER_UPDATE_NODE);
    }
    FlowOrder flowOrder = flowOrderServiceI.getFlowOrderById(flowId);
    //校验userId是否在待审批人中
    if (!flowOrder.getNextApproveUsers().contains(userId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_UPDATE_NODE_USER);
    }
    String processInstanceId = flowOrder.getProcInstId();
    //获取当前任务节点
    Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
        .taskCandidateOrAssigned(userId.trim()).singleResult();
    //替换操作
    if (Constant.FLOW_UPDATE_APPROVER.equals(operate)) {
      //将原审批人从task中移除
      taskService.deleteCandidateUser(task.getId(), userId.trim());
      //将替换人添加到activiti节点
      Arrays.stream(userIds.split(","))
          .forEach(user -> taskService.addCandidateUser(task.getId(), user));
      //将替换人添加到flowOrder,包括查阅权限和审批权限
      String newNextApproveUsers = flowOrder.getNextApproveUsers().replaceAll(userId, userIds);
      String newQueryUsers = flowOrder.getQueryUsers().replaceFirst(userId, userIds);
      flowOrder.setNextApproveUsers(newNextApproveUsers);
      flowOrder.setQueryUsers(newQueryUsers);
      flowOrderServiceI.save(flowOrder);
      //给替换人发送通知
      notifyServiceI.sendNotify(
          new Notify(flowOrder.getApplyUserName(), flowOrder.getApplyUserId(), "发起了",
              flowOrder.getApplyTypeName(), 0,
              flowOrder.getId()), getAgentUsers(userIds, flowOrder.getApplyTypeKey()));
      //添加操作
    } else if (Constant.FLOW_ADD_APPROVER.equals(operate)) {
      //将替换人添加到activiti节点
      Arrays.stream(userIds.split(","))
          .forEach(user -> taskService.addCandidateUser(task.getId(), user));
      //将添加人添加到flowOrder,包括查阅权限和审批权限
      String newNextApproveUsers = flowOrder.getNextApproveUsers() + "," + userIds + ",";
      String newQueryUsers = flowOrder.getQueryUsers() + "," + userIds + ",";
      flowOrder.setNextApproveUsers(newNextApproveUsers);
      flowOrder.setQueryUsers(newQueryUsers);
      flowOrderServiceI.save(flowOrder);
      //给替换人发送通知
      notifyServiceI.sendNotify(
          new Notify(flowOrder.getApplyUserName(), flowOrder.getApplyUserId(), "发起了",
              flowOrder.getApplyTypeName(), 0,
              flowOrder.getId()), getAgentUsers(userIds, flowOrder.getApplyTypeKey()));
      //删除操作
    } else if (Constant.FLOW_DELETE_APPROVER.equals(operate)) {
      final String[] nextApproveUsers = {flowOrder.getNextApproveUsers()};
      if (taskService.getIdentityLinksForTask(task.getId()).size() <= userIds.split(",").length) {
        throw new FlowException(ResultEnum.FLOW_ORDER_OPERATE_DELETE);
      }
      Arrays.stream(userIds.split(",")).forEach(user -> {
        taskService.deleteCandidateUser(task.getId(), user.trim());
        nextApproveUsers[0] = nextApproveUsers[0].replaceAll(user.trim(), Constant.FILL_BLANK);
      });
      flowOrder.setNextApproveUsers(nextApproveUsers[0]);
      flowOrderServiceI.save(flowOrder);
    } else {
      throw new FlowException(ResultEnum.FLOW_ORDER_OPERATE_TYPE);
    }
    //增加日志
    String newMemo = "[系统管理员处理]" + "[" + operate + "]" + memo;
    approveLogServiceI.createApproveLog(processInstanceId, task.getId(), task.getTaskDefinitionKey(), task.getName(),
        flowOrder.getApplyTypeName(), Constant.FLOW_SYSTEM_MANAGER, newMemo,
        SecurityUtils.currentUsername(), "系统管理员");

    return flowOrder.getNextApproveUsers();
  }

  /**
   * 撤销流程
   *
   * @param flowId 流程表单Id
   * @return 流程是否发起成功
   */
  @Override
  @Transactional
  public String revokeProcessInstance(String flowId, String reason) {
    FlowOrder flowOrder;
    try {
      flowOrder = flowOrderRepository.findById(flowId).get();
    } catch (Exception e) {
      log.error("流程不存在", e);
      throw new FlowException(ResultEnum.ACTIVITI_FLOW_NOT);
    }
    String currentUserId = SecurityUtils.currentUsername();
    String currentUserName = SecurityUtils.currentUserInfo().getCname();
    //校验是否能够撤销
    checkRevoke(flowOrder, currentUserId);
    //流程撤销
    activitiServiceI.revokeProcessInstance(flowOrder.getProcInstId(), reason);
    //创建日志
    approveLogServiceI.createApproveLog(flowOrder.getProcInstId(), "", Constant.FLOW_REVOKE, Constant.FLOW_REVOKE_NODE,
        flowOrder.getApplyTypeName(), Constant.FLOW_REVOKE, Constant.FLOW_REVOKE_Z + reason, currentUserId, currentUserName);
    //给当前任务审批人发送通知
    String notifyUsers = flowOrder.getNextApproveUsers();
    notifyServiceI.sendNotify(new Notify(flowOrder.getApplyUserName(), flowOrder.getApplyUserId(), "撤销了",
        flowOrder.getApplyTypeName(), 3, flowOrder.getId()), getAgentUsers(notifyUsers, flowOrder.getApplyTypeKey()));
    //给撤销人发送通知
    notifyServiceI.sendNotify(new Notify("你", flowOrder.getApplyUserId(), "撤销了",
        flowOrder.getApplyTypeName(), 1, flowOrder.getId()), flowOrder.getApplyUserId());
    //修改流程表单flowOrder状态为已撤销
    flowOrder.setApplyStatus(2);
    flowOrder.setNextApproveUsers("");
    flowOrderRepository.save(flowOrder);
    return "撤销流程成功";
  }

  /**
   * 检验撤销权限
   */
  private Boolean checkRevoke(FlowOrder flowOrder, String currentUserId) {
    if (!flowOrder.getApplyUserId().contains(currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_REVOKE_NOT_APPLY_USER);
    }
    if (StringUtils.isNotBlank(flowOrder.getApprovedUsers().replaceAll(",", Constant.FILL_BLANK))) {
      throw new FlowException(ResultEnum.FLOW_ORDER_REVOKE_APPROVED);
    }
    return true;
  }

}
