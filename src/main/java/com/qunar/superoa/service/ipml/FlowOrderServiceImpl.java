package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.dto.*;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.Agent;
import com.qunar.superoa.model.ApproveLog;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.model.FlowOrder;
import com.qunar.superoa.model.FormData;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ActivitiServiceI;
import com.qunar.superoa.service.AgentServiceI;
import com.qunar.superoa.service.ApproveLogServiceI;
import com.qunar.superoa.service.FlowModelServiceI;
import com.qunar.superoa.service.FlowOrderServiceI;
import com.qunar.superoa.service.FormDataServiceI;
import com.qunar.superoa.service.ExtSysUnapproveFlowServiceI;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 11:13 2018/12/21
 * @Modify by:
 */
@Service
@Slf4j
public class FlowOrderServiceImpl implements FlowOrderServiceI {

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  @Autowired
  private AgentServiceI agentServiceI;

  @Autowired
  private ApproveLogServiceI approveLogServiceI;

  @Autowired
  private FlowModelServiceI flowModelServiceI;

  @Autowired
  private FormDataServiceI formDataServiceI;

  @Autowired
  private UserService userService;

  @Autowired
  private ActivitiServiceI activitiServiceI;

  @Autowired
  private ExtSysUnapproveFlowServiceI unapproveFlowServiceI;

  @Override
  public Optional<List<FlowOrder>> findByApplyStatus(int status) {
    return flowOrderRepository.findByApplyStatus(status);
  }

  @Override
  public FlowOrder getFlowOrderById(String flowId) {
    return flowOrderRepository.findById(flowId).get();
  }

  @Override
  public FlowOrder save(FlowOrder flowOrder) {
    return flowOrderRepository.save(flowOrder);
  }

  /**
   * 审批修改flowOrder
   */
  @Override
  public FlowOrder approveUpdateFlowOrder(FlowOrder flowOrder, String nextUsers,
                                          String nestApproveNotifyUsers,
                                          String currentUserId, String agentUser, String notifyUsers, long taskNum, String type) {
    flowOrder.setNextApproveUsers(nextUsers);
    //taskNum为0  认为流程已经结束
    if (Constant.FLOW_REJECT.equals(type)) {
      flowOrder.setLastApproveTime(DateTimeUtil.getDateTime());
      flowOrder.setApplyStatus(5);
    } else if (taskNum == 0) {
      flowOrder.setLastApproveTime(DateTimeUtil.getDateTime());
      flowOrder.setApplyStatus(0);
    }
    flowOrder.setUpdateTime(DateTimeUtil.getDateTime());
    flowOrder.setLastApproveUserId(currentUserId);
    //有代理人  已审批人 + (currentUser代理agentUser审批)
    String approvedUsers = StringUtils.isBlank(agentUser) ?
        currentUserId : currentUserId + ",(代理" + agentUser + "审批)";
    flowOrder.setApprovedUsers(flowOrder.getApprovedUsers() + approvedUsers + ",");
    flowOrder.setQueryUsers(
        flowOrder.getQueryUsers() + "," + currentUserId + "," + nestApproveNotifyUsers + ","
            + notifyUsers + ",");
    if (StringUtils.isNotBlank(notifyUsers)) {
      flowOrder.setNotifyUsers(flowOrder.getNotifyUsers() + notifyUsers);
    }
    return save(flowOrder);
  }

  /**
   * 获取当前用户草稿、审批中、已完结流程数
   */
  @Override
  public FlowCountDto getFlowCount() {
    String currentUserId = SecurityUtils.currentUsername();
    FlowCountDto flowCountDto = new FlowCountDto();
    flowCountDto
        .setApprovalCount(flowOrderRepository.countByApplyUserIdAndApplyStatus(currentUserId, 1));
    flowCountDto.setDraftCount(
        flowOrderRepository.countByApplyUserIdAndApplyStatus(currentUserId, 3));
    flowCountDto
        .setRevokeCount(flowOrderRepository.countByApplyUserIdAndApplyStatus(currentUserId, 2));
    flowCountDto.setOffCount(
        flowOrderRepository.countByApplyUserIdAndApplyStatus(currentUserId, 0) +
            flowOrderRepository.countByApplyUserIdAndApplyStatus(currentUserId, 5));
    flowCountDto.setOaUnApproveCount(getToApproveCount(currentUserId));
    //外部系统待审批流程数
    flowCountDto.setExtSysUnApproveCount(unapproveFlowServiceI.getExtSysToApproveCount());
    return flowCountDto;
  }

  /**
   * 获取我的待办、已办流程数(当前登录人)
   */
  @Override
  public ApproveCountDto getApproveCount() {
    String currentUserId = SecurityUtils.currentUsername();
    ApproveCountDto approveCountDto = new ApproveCountDto();
    approveCountDto.setApproveCount(getToApproveCount(currentUserId));
    approveCountDto.setApproved(flowOrderRepository
        .countByApprovedUsersLikeOrApprovedUsersLike("%," + currentUserId + ",%",
            "%,(代理" + currentUserId + "审批%"));
    approveCountDto.setExtSysApproveCount(unapproveFlowServiceI.getExtSysToApproveCount());
    return approveCountDto;
  }

  /**
   * 查询代办数
   */
  @Override
  public int getToApproveCount(String qtalk) {
    return (int) flowOrderRepository.count((Specification<FlowOrder>) (root, query, cb) -> {
      //当前登录用户名称
      Predicate predicate = cb.conjunction();

      Optional<List<Agent>> agents = agentServiceI.getAgentByAgent(qtalk);
      //需求：查询待审批的流程   代码逻辑：当前用户是否有被代理人
      if (agents.isPresent()) {
        List<Agent> agentList = agents.get();
        Predicate[] predicates = new Predicate[agentList.size() + 1];
        predicates[0] = cb.like(root.get("nextApproveUsers"), "%," + qtalk + ",%");
        agentList.forEach(agent -> {
          if ("all".equalsIgnoreCase(agent.getProcessID())) {
            //代理被代理人的全部流程  则直接查询被代理人的流程
            predicates[agentList.indexOf(agent) + 1] = cb.like(root.get("nextApproveUsers"),
                "%," + agent.getQtalk() + ",%");
          } else {
            //代理被代理人的部分流程
            //需求：查询全部流程  则查询代理的所有指定流程   SQL逻辑 or (user and (flow or flow...))
            List<String> processIds = Arrays.asList(agent.getProcessID().split(","));
            Predicate[] predicatesProcIds = new Predicate[processIds.size()];
            processIds.forEach(
                processId -> {
                  if (StringUtils.isNotBlank(processId)) {
                    predicatesProcIds[processIds.indexOf(processId)] = cb
                        .equal(root.get("applyTypeKey"), processId);
                  }
                }
            );
            predicates[agentList.indexOf(agent) + 1] = cb
                .and(cb.like(root.get("nextApproveUsers"),
                    "%," + agent.getQtalk() + ",%"), cb.or(predicatesProcIds));
          }
        });
        predicate.getExpressions().add(cb.or(predicates));
      } else {
        predicate.getExpressions().add(
            cb.like(root.get("nextApproveUsers"), "%," + qtalk + ",%"));
      }
      return predicate;
    });
  }

  /**
   * 查询流程详情
   */
  @Override
  public FlowDataDto getFlowById(String flowId) {
    return getFlowById(flowId, SecurityUtils.currentUsername());
  }

  /**
   * 移动端查询流程详情
   */
  @Override
  public FlowDataDto getFlowById(String flowId, String currentUserId) {

    log.info("查询操作  flowId:{},currentUserId:{}", flowId, currentUserId);
    if (!checkQuery(flowId, currentUserId)) {
      throw new FlowException(ResultEnum.FLOW_ORDER_NOT_QUERY);
    }
    FlowOrder flowOrder = getFlowOrderById(flowId);
    FormData formData = formDataServiceI.findByProcInstId(flowOrder.getProcInstId());
    FlowModel flowModel = flowModelServiceI
        .getFlowModelByFormKeyAndVersion(flowOrder.getApplyTypeKey(), formData.getFormVersion());
    FlowDataDto flowDataDto = createFlowDataDto(flowOrder);
    //检验是否可撤回
    if (flowOrder.getApplyUserId().contains(currentUserId)
        && StringUtils.isBlank(flowOrder.getApprovedUsers().replaceAll(",", Constant.FILL_BLANK))
        && flowOrder.getApplyStatus() == 1) {
      flowDataDto.setCanRevokeOrBack(Constant.FLOW_CAN_REVOKE);
    }
    //设置fromModel
    flowDataDto.setFormModelJson(flowModel.getFormModels());
    //设置表单特殊字段集合
    Task task = activitiServiceI
        .getTaskByProcInstIdAndUser(flowOrder.getProcInstId(), currentUserId);
    //节点非加签状态  获取此节点的可编辑字段
    if (task != null && (task.getDelegationState() == null ||
        !Constant.FLOW_COUNTERSIGN_ING.equalsIgnoreCase(task.getDelegationState().name()))) {
      flowDataDto.setEditNodeName(getEditColumnByTaskName(task.getName(), flowModel.getEditNode()));
    } else if (task != null && flowOrder.getApplyUserId()
        .contains(String.valueOf(task.getAssignee())) && Constant.FLOW_COUNTERSIGN_ING
        .equalsIgnoreCase(task.getDelegationState().name())) {
      flowDataDto.setEditNodeName(getEditColumnByTaskName("申请人", flowModel.getEditNode()));
    } else {
      flowDataDto.setEditNodeName(getEditColumnByTaskName("", ""));
    }
    return flowDataDto;
  }

  /**
   * 创建FlowDataDto
   */
  private FlowDataDto createFlowDataDto(FlowOrder flowOrder) {
    FlowDataDto flowDataDto = new FlowDataDto();
    //设置基础信息
    flowDataDto.setApplyUserId(flowOrder.getApplyUserId());
    flowDataDto.setApplyUserName(flowOrder.getApplyUserName());
    flowDataDto.setApplyUserDept(flowOrder.getApplyUserDept());
    flowDataDto.setApplyUserFullDept(flowOrder.getApplyUserFullDept());
    flowDataDto.setApplyTime(flowOrder.getApplyTime());
    flowDataDto.setFlowName(flowOrder.getApplyTypeName());
    flowDataDto.setProcInstId(flowOrder.getProcInstId());
    flowDataDto.setFlowKey(flowOrder.getApplyTypeKey());

    //设置FormData
    FormDataDto formDataDto = formDataServiceI.getFromDataByProcInstId(flowOrder.getProcInstId());
    //对草稿单独处理
    if (flowOrder.getApplyStatus() == 3) {
      formDataDto = formDataServiceI.getFromDataByProcInstId(flowOrder.getId());
    }
    flowDataDto.setFormDatas(formDataDto.getFormDatas());

    //设置审批日志
    List<ApproveLogDto> approveLogDtos = new ArrayList<>();
    approveLogServiceI.findByOidOrderByApproveTime(flowOrder.getLogOid())
        .ifPresent(approveLogs -> {
          approveLogs.stream().forEach(approveLog -> {
            ApproveLogDto approveLogDto = new ApproveLogDto();
            approveLogDto.setApproveTime(approveLog.getApproveTime());
            approveLogDto.setApproveUserId(approveLog.getApproveUserId());
            approveLogDto.setApproveUserAvatar(
                userService.findUserAvatarByName(approveLog.getApproveUserId()));
            approveLogDto.setMemo(approveLog.getMemo());
            approveLogDto.setNodeName(approveLog.getTaskName());
            approveLogDto.setNextNodeName(approveLog.getNextTaskName());
            approveLogDtos.add(approveLogDto);
          });
        });
    flowDataDto.setApproveLogs(approveLogDtos);
    //设置下一节点审批信息
    setFlowOrderNestApprove(flowOrder, flowDataDto);
    return flowDataDto;
  }

  /**
   * 设置下一节点审批信息
   */
  private void setFlowOrderNestApprove(FlowOrder flowOrder, FlowDataDto flowDataDto) {
    //设置下一审批人，头像及审批节点名称
    if (flowOrder.getApplyStatus() == 3) {
      flowDataDto.setApproveNodeName("");
      flowDataDto.setApproveUsers("");
      flowDataDto.setApproveUsersAvatar("");
    } else if (flowOrder.getApplyStatus() == 0 || flowOrder.getApplyStatus() == 5
        || flowOrder.getApplyStatus() == 2) {
      flowDataDto.setApproveUsersAvatar(Constant.OA_END_AVATAR);
      flowDataDto.setApproveUsers("");
      flowDataDto.setApproveNodeName("流程审批结束");
    } else {
      List<String> approveUsersList = getAgents(flowOrder.getNextApproveUsers(),
          flowOrder.getApplyTypeKey());
      if (approveUsersList.size() == 1) {
        flowDataDto
            .setApproveUsersAvatar(userService.findUserAvatarByName(approveUsersList.get(0)));
        flowDataDto.setApproveUsers(approveUsersList.get(0));
      } else if (approveUsersList.size() > 1) {
        flowDataDto.setApproveUsersAvatar(Constant.OA_GROUP_AVATAR);
        StringBuffer approveUsers = new StringBuffer();
        approveUsersList.forEach(user -> approveUsers.append(user).append(","));
        approveUsers.deleteCharAt(approveUsers.length() - 1);
        flowDataDto.setApproveUsers(approveUsers.toString());
      }
      //查询当前task
      List<Task> tasks = activitiServiceI.getTaskByProcInstId(flowOrder.getProcInstId());
      StringBuffer approveNode = new StringBuffer();
      tasks.forEach(task -> {
        ApproveLog approveLog = approveLogServiceI
            .getApproveLogByProInstIdAndTaskId(flowOrder.getProcInstId(), task.getId());
        if (approveLog == null) {
          approveNode.append(task.getName()).append(",");
        } else {
          if (Constant.FLOW_COUNTERSIGN.equalsIgnoreCase(approveLog.getManagerType())) {
            //加签
            approveNode.append(Constant.FLOW_COUNTERSIGN_USER).append(",");
          } else if (Constant.FLOW_FORWARD.equalsIgnoreCase(approveLog.getManagerType())) {
            //转交
            approveNode.append(Constant.FLOW_FORWARD_USER).append(",");
          } else {
            approveNode.append(StringUtils.isNotBlank(approveLog.getNextTaskName()) ?
                approveLog.getNextTaskName() : task.getName()).append(",");
          }
        }
      });
      //查询当前节点的最后一条日志
      flowDataDto.setApproveNodeName(
          "待办/" + approveNode.deleteCharAt(approveNode.length() - 1).toString());
    }
  }

  /**
   * 当前节点可编辑字段
   *
   * @return 字符串，内容为当前任务可编辑表单字段，用逗号分隔 - 若为空串，则表示不可编辑
   */
  private List getEditColumnByTaskName(String taskName, String editNode) {

    List editNodeList = Lists.newArrayList();
    //可编辑节点为空，返回空列表
    if (StringUtils.isBlank(editNode)) {
      return editNodeList;
    }
    //将json格式转换为map
    Map<String, Object> editNodeMaps;
    try {
      editNodeMaps = CommonUtil.s2m(editNode);
    } catch (Exception e) {
      log.error("editNode字段Json串反序列化失败", e);
      throw new FlowException(ResultEnum.FLOW_MODEL_EDITNODE_ERROE);
    }
    //可编辑节点中未包含当前任务名称，返回空列表
    if (!editNodeMaps.keySet().contains(taskName)) {
      return editNodeList;
    }
    //返回当前任务的表单可编辑字段名称
    return (List) editNodeMaps.get(taskName);
  }

  /**
   * 判断当前登录用户是否有查看当前单子的查看权限
   */
  private boolean checkQuery(String flowOrderId, String currentUserId) {
    FlowOrder flowOrder = getFlowOrderById(flowOrderId);
    if (StringUtils.isBlank(flowOrder.getApplyUserId())) {
      return false;
    } else {
      String queryUsers = flowOrder.getQueryUsers();
      if (queryUsers.contains("," + currentUserId + ",")) {
        return true;
      } else {
        List<String> users = getAgent(currentUserId, flowOrder.getApplyTypeKey());
        for (String user : users) {
          if (flowOrder.getNextApproveUsers().contains("," + user + ",")) {
            return true;
          }
        }
        return false;
      }
    }
  }

  /**
   * 查看users中是否有代理人并标记
   */
  private List<String> getAgent(String user, String flowModelKey) {
    Optional<List<Agent>> agentsOptional = agentServiceI.getAgentByAgent(user);
    List<String> users = new ArrayList<>();
    if (!agentsOptional.isPresent()) {
      return users;
    } else {
      List<Agent> agents = agentsOptional.get();
      agents.forEach(agent -> {
        if (agent != null && ("all".equalsIgnoreCase(agent.getProcessID()) || ("," + agent
            .getProcessID() + ",").contains("," + flowModelKey + ","))) {
          users.add(agent.getQtalk());
        }
      });
      return users;
    }
  }

  /**
   * 获取users中所有代理人  并去重
   */
  private List<String> getAgents(String users, String flowModelKey) {
    Map<String, Object> map = new HashMap<>();
    Arrays.stream(users.split(",")).forEach(user -> {
      if (StringUtils.isNotBlank(user)) {
        map.put(user, "");
        List<Agent> agentList = agentServiceI.getAgentByQtalk(user);
        if (agentList != null) {
          agentList.forEach(agent -> {
            if (agent != null && ("all".equalsIgnoreCase(agent.getProcessID()) || ("," + agent
                .getProcessID() + ",").contains("," + flowModelKey + ","))) {
              map.put(agent.getAgent(), "");
            }
          });
        }
      }
    });
    List<String> userList = new ArrayList<>();
    map.keySet().forEach(user -> userList.add(user));
    return userList;
  }

  /**
   * 根据查询条件获取flowOrder Page
   *
   * @param queryFlowDto
   * @return
   */
  private Page getPage(QueryFlowDto queryFlowDto) {
    Page page = null;
    String currentUserId = SecurityUtils.currentUsername();
    List<Integer> status = new ArrayList<>();
    if ("4".equals(queryFlowDto.getStatus())) {
      status.add(0);
      status.add(1);
      status.add(2);
      status.add(3);
      status.add(5);
    } else {
      status.add(Integer.parseInt(queryFlowDto.getStatus()));
    }
    String applyBeginTime = StringUtils.isNotBlank(queryFlowDto.getSubmitBeginTime()) ? queryFlowDto.getSubmitBeginTime() : "2000-01-01";
    String applyEndTime = StringUtils.isNotBlank(queryFlowDto.getSubmitEndTime()) ? queryFlowDto.getSubmitEndTime() : "2100-01-01";
    if (Constant.FLOW_ORDER_MY_QUERY_TYPE.equals(queryFlowDto.getQueryType())) {
      //我发起的
      if (StringUtils.isNotBlank(queryFlowDto.getFinishBeginTime()) && StringUtils.isNotBlank(queryFlowDto.getFinishEndTime())) {
        page = flowOrderRepository.findMyStartFlowOrderTime(currentUserId, queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(), status,
            applyBeginTime.trim(), applyEndTime.trim(), queryFlowDto.getFinishBeginTime().trim(), queryFlowDto.getFinishEndTime().trim(), queryFlowDto.getPageAble());
      } else {
        page = flowOrderRepository.findMyStartFlowOrder(currentUserId, queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(), status,
            applyBeginTime.trim(), applyEndTime.trim(), queryFlowDto.getPageAble());
      }
//      queryFlowDto.setUserId(currentUserId);
//      page = getFlowsByDto(queryFlowDto, "", currentUserId);
    } else if (Constant.FLOW_ORDER_APPROVED_QUERY_TYPE.equals(queryFlowDto.getQueryType())) {
      //我已审批
      if (StringUtils.isNotBlank(queryFlowDto.getFinishBeginTime()) && StringUtils.isNotBlank(queryFlowDto.getFinishEndTime())) {
        page = flowOrderRepository.findMyApprovedFlowOrderTime("," + currentUserId + ",", ",(代理" + currentUserId + "审批)",
            queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(), queryFlowDto.getUserId().trim(), status, applyBeginTime.trim(), applyEndTime.trim(),
            queryFlowDto.getFinishBeginTime().trim(), queryFlowDto.getFinishEndTime().trim(), queryFlowDto.getPageAble());
      } else {
        page = flowOrderRepository.findMyApprovedFlowOrder("," + currentUserId + ",", ",(代理" + currentUserId + "审批)",
            queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(), queryFlowDto.getUserId().trim(), status, applyBeginTime.trim(), applyEndTime.trim(), queryFlowDto.getPageAble());
      }
//      getFlowsByDto(queryFlowDto, Constant.FLOW_ORDER_APPROVED, currentUserId);
    } else if (Constant.FLOW_ORDER_TO_APPROVE_QUERY_TYPE.equals(queryFlowDto.getQueryType())) {
      //待我审批
      //查询当前登录人和代理人的待审批
      List<String> users = getQueryAgentUser(queryFlowDto.getFlowType(), currentUserId);
      page = flowOrderRepository.findNeedMyApproveFlowOrder(queryFlowDto.getKey().trim(), queryFlowDto.getUserId().trim(), queryFlowDto.getFlowType().trim(),
          users.get(0), users.get(1).split(";")[0], users.get(2).split(";")[0], users.get(3).split(";")[0], users.get(4).split(";")[0], users.get(5).split(";")[0],
          users.get(6).split(";")[0], users.get(7).split(";")[0], users.get(8).split(";")[0], users.get(9).split(";")[0], users.get(10).split(";")[0],
          applyBeginTime.trim(), applyEndTime.trim(), Arrays.asList(users.get(1).split(";")[1].split(",")), Arrays.asList(users.get(2).split(";")[1].split(",")),
          Arrays.asList(users.get(3).split(";")[1].split(",")), Arrays.asList(users.get(4).split(";")[1].split(",")), Arrays.asList(users.get(5).split(";")[1].split(",")),
          Arrays.asList(users.get(6).split(";")[1].split(",")), Arrays.asList(users.get(7).split(";")[1].split(",")), Arrays.asList(users.get(8).split(";")[1].split(",")),
          Arrays.asList(users.get(9).split(";")[1].split(",")), Arrays.asList(users.get(10).split(";")[1].split(",")), queryFlowDto.getPageAble());
//      page = getToApproveFlowsByDto(queryFlowDto, currentUserId);
    } else if (Constant.FLOW_ORDER_NOTIFY_TO_ME_TYPE.equals(queryFlowDto.getQueryType())) {
      //知会给我的
      if (StringUtils.isNotBlank(queryFlowDto.getFinishBeginTime()) && StringUtils.isNotBlank(queryFlowDto.getFinishEndTime())) {
        page = flowOrderRepository.findNotifyMeFlowOrderTime(queryFlowDto.getUserId().trim(), queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(),
            "," + currentUserId + ",", applyBeginTime.trim(), applyEndTime.trim(), queryFlowDto.getFinishBeginTime().trim(), queryFlowDto.getFinishEndTime().trim(), queryFlowDto.getPageAble());
      } else {
        page = flowOrderRepository.findNotifyMeFlowOrder(queryFlowDto.getUserId().trim(), queryFlowDto.getKey().trim(), queryFlowDto.getFlowType().trim(),
            "," + currentUserId + ",", applyBeginTime.trim(), applyEndTime.trim(), queryFlowDto.getPageAble());
      }
//      page = getNotifyToMeFlowsByDto(queryFlowDto, currentUserId);
    }
    return page;
  }

  private List<String> getQueryAgentUser(String flowKey, String currentUserId) {
    List<String> users = new ArrayList<>();
    users.add("," + currentUserId + ",");
    Optional<List<Agent>> agents = agentServiceI.getAgentByAgent(currentUserId);
    List<FlowModelNameDto> flowModelNameDtos = flowModelServiceI.getFlowModelsByDept("");
    StringBuilder flowKeys = new StringBuilder();
    for (FlowModelNameDto flowModelNameDto : flowModelNameDtos) {
      flowKeys.append(flowModelNameDto.getFormKey()).append(",");
    }
    if (agents.isPresent()) {
      if (StringUtils.isBlank(flowKey)) {
        agents.get().forEach(agent -> {
          if (!agent.getProcessID().equalsIgnoreCase("all")) {
            users.add("," + agent.getQtalk() + ",;" + agent.getProcessID());
          } else {
            users.add("," + agent.getQtalk() + ",;" + flowKeys.toString());
          }
        });
      } else {
        agents.get().forEach(agent -> {
          if ("all".equalsIgnoreCase(agent.getProcessID()) || ("," + agent.getProcessID() + ",").contains("," + flowKey + ",")) {
            users.add("," + agent.getQtalk() + ",;" + agent.getProcessID());
          }
        });
      }
    }
    while (users.size() < 11) {
      users.add(",placeholder,;placeholderKey");
    }
    return users;
  }

  @Override
  public PageResult<FlowOrderDto> getMyFlows(QueryFlowDto queryFlowDto) {
    PageResult<FlowOrderDto> pageResult = new PageResult<>();
    Page<FlowOrder> page = getPage(queryFlowDto);
    List<FlowOrderDto> flowOrderDtos = new ArrayList<>();
    if (page != null) {
      Optional.of(page.getContent())
          .ifPresent(flowOrders -> flowOrders.stream().forEach(flowOrder -> {
            //下一节点审批人添加代理人转化
            flowOrder.setNextApproveUsers(
                getAgentUser(flowOrder.getNextApproveUsers(), flowOrder.getApplyTypeKey()));
            FlowOrderDto flowOrderDto = new FlowOrderDto();
            //添加申请人头像URL
            FormData formData = formDataServiceI.findByProcInstId(flowOrder.getProcInstId());
            //floworder 转为 flowoder dto 并添加头像URL
            flowOrderDto.setFlowOrder(new com.qunar.superoa.dto.FlowOrder(flowOrder,
                userService.findUserAvatarByName(flowOrder.getApplyUserId())));
            FlowModelDto flowModelDto = flowModelServiceI
                .getFlowModelDtoByFormKeyAndVersion(flowOrder.getApplyTypeKey(),
                    formData.getFormVersion());
            flowOrderDto.setFormModelJson(
                flowModelDto == null ? new FlowModelDto()
                    : new Gson().toJson(flowModelDto.getFormModelJson()));
            flowOrderDto.setFormDatas(
                formDataServiceI.getFromDataByProcInstId(flowOrder.getProcInstId()).getFormDatas());
            flowOrderDtos.add(flowOrderDto);
          }));
      pageResult.setContent(flowOrderDtos);
      pageResult.setPageable(page.getPageable());
      pageResult.setTotal((int) page.getTotalElements());
    }
    return pageResult;
  }

  /**
   * 查看users中是否有代理人并标记
   */
  private String getAgentUser(String users, String flowModelKey) {
    StringBuffer userResult = new StringBuffer();
    Arrays.stream(users.split(",")).forEach(
        user -> {
          if (StringUtils.isNotBlank(user)) {
            List<Agent> agentList = agentServiceI.getAgentByQtalk(user);
            userResult.append(user);
            if (agentList != null) {
              agentList.forEach(agent -> {
                if (agent != null && ("all".equalsIgnoreCase(agent.getProcessID()) || ("," + agent
                    .getProcessID() + ",").contains("," + flowModelKey + ","))) {
                  userResult.append("(").append(agent.getAgent()).append("代理)");
                }
              });
            }
            userResult.append(",");
          }
        }
    );
    return userResult.length() > 0 ? userResult.deleteCharAt(userResult.length() - 1).toString()
        : userResult.toString();
  }

  /**
   * 获取流程实例FlowOrder
   */
  public Page<FlowOrder> getFlowsByDto(QueryFlowDto queryFlowDto, String type,
                                       String currentUserId) {
    return flowOrderRepository.findAll((Specification<FlowOrder>) (root, query, cb) -> {
      Predicate predicate = createPredicate(root, cb, queryFlowDto);
      if (Constant.FLOW_ORDER_APPROVED.equals(type)) {
        //查询已经审批的流程  和别人代理我审批的流程
        predicate.getExpressions()
            .add(cb.or(cb.like(root.get("approvedUsers"), "%," + currentUserId + ",%"),
                cb.like(root.get("approvedUsers"), "%,(代理" + currentUserId + "审批),%")));
      }
      if (StringUtils.isNotBlank(queryFlowDto.getStatus()) && !"4"
          .equals(queryFlowDto.getStatus())) {
        predicate.getExpressions()
            .add(cb.equal(root.get("applyStatus"), Integer.parseInt(queryFlowDto.getStatus())));
      }
      return predicate;
    }, queryFlowDto.getPageAble());
  }

  /**
   * 获取待我审批流程实例FlowOrder
   */
  public Page<FlowOrder> getToApproveFlowsByDto(QueryFlowDto queryFlowDto, String currentUserId) {
    return flowOrderRepository.findAll((Specification<FlowOrder>) (root, query, cb) -> {
      Predicate predicate = createPredicate(root, cb, queryFlowDto);

      Optional<List<Agent>> agents = agentServiceI.getAgentByAgent(currentUserId);
      //需求：查询待审批的流程   代码逻辑：当前用户是否代理别人
      if (agents.isPresent()) {
        List<Agent> agentList = agents.get();
        Predicate[] predicates = new Predicate[agentList.size() + 1];
        predicates[0] = cb.like(root.get("nextApproveUsers"), "%," + currentUserId + ",%");
        agentList.forEach(agent -> {
          if ("all".equalsIgnoreCase(agent.getProcessID())) {
            //代理被代理人的全部流程  则直接查询被代理人的流程
            predicates[agentList.indexOf(agent) + 1] = cb.like(root.get("nextApproveUsers"),
                "%," + agent.getQtalk() + ",%");
          } else {
            //代理被代理人的部分流程
            if (StringUtils.isNotBlank(queryFlowDto.getFlowType())) {
              //需求：查询指定流程
              if (("," + agent.getProcessID()).contains(queryFlowDto.getFlowType())) {
                //如果查询流程和代理流程一致   则查询被代理人的流程
                predicates[agentList.indexOf(agent) + 1] = cb.like(root.get("nextApproveUsers"),
                    "%," + agent.getQtalk() + ",%");
              }
            } else {
              //需求：查询全部流程  则查询代理的所有指定流程   SQL逻辑 or (user and (flow or flow...))
              List<String> processIds = Arrays.asList(agent.getProcessID().split(","));
              Predicate[] predicatesProcIds = new Predicate[processIds.size()];
              processIds.forEach(
                  processId -> {
                    if (StringUtils.isNotBlank(processId)) {
                      predicatesProcIds[processIds.indexOf(processId)] = cb
                          .equal(root.get("applyTypeKey"), processId);
                    }
                  }
              );
              predicates[agentList.indexOf(agent) + 1] = cb
                  .and(cb.like(root.get("nextApproveUsers"),
                      "%," + agent.getQtalk() + ",%"), cb.or(predicatesProcIds));
            }
          }
        });
        predicate.getExpressions().add(cb.or(predicates));
      } else {
        predicate.getExpressions().add(
            cb.like(root.get("nextApproveUsers"), "%," + currentUserId + ",%"));
      }
      predicate.getExpressions()
          .add(cb.equal(root.get("applyStatus"), 1));
      return predicate;
    }, queryFlowDto.getPageAble());
  }

  /**
   * 获取知会给我等流程实例FlowOrder
   */
  public Page<FlowOrder> getNotifyToMeFlowsByDto(QueryFlowDto queryFlowDto, String currentUserId) {
    return flowOrderRepository.findAll((Specification<FlowOrder>) (root, query, cb) -> {
      Predicate predicate = createPredicate(root, cb, queryFlowDto);
      predicate.getExpressions().add(
          cb.like(root.get("notifyUsers"), "%," + currentUserId + ",%"));
      return predicate;
    }, queryFlowDto.getPageAble());
  }

  /**
   * 创建查询条件  添加按时间 发起人 流程类型等条件
   */
  private Predicate createPredicate(Path root, CriteriaBuilder cb, QueryFlowDto queryFlowDto) {
    Predicate predicate = cb.conjunction();
    if (StringUtils.isNotBlank(queryFlowDto.getSubmitBeginTime())) {
      predicate.getExpressions().add(
          cb.between(root.get("applyTime"), "%" + queryFlowDto.getSubmitBeginTime().trim() + "%",
              "%" + queryFlowDto.getSubmitEndTime().trim() + "%"));
    }
    if (StringUtils.isNotBlank(queryFlowDto.getFinishBeginTime())) {
      predicate.getExpressions().add(cb.between(root.get("lastApproveTime"),
          "%" + queryFlowDto.getFinishBeginTime().trim() + "%",
          "%" + queryFlowDto.getFinishEndTime().trim() + "%"));
    }
    if (StringUtils.isNotBlank(queryFlowDto.getUserId())) {
      predicate.getExpressions().add(cb.equal(root.get("applyUserId"), queryFlowDto.getUserId()));
    }
    // 关联查询join   http://www.voidcn.com/article/p-rwmqxeep-brn.html
    //TODO 添加模糊查询摘要
    if (StringUtils.isNotBlank(queryFlowDto.getKey())) {
      predicate.getExpressions()
          .add(cb.like(root.get("headline"), "%" + queryFlowDto.getKey() + "%"));
//                    predicate.getExpressions().add(cb.equal(root.get("applyUserId"), queryFlowDto.getKey()));
//                    Join<FlowOrder, FormData> formDataJoin = root.join("procInstId", JoinType.LEFT);
//                    predicate.getExpressions().add(cb.like(formDataJoin.get("fromDatas"), queryFlowDto.getKey()));
    }
    if (StringUtils.isNotBlank(queryFlowDto.getFlowType())) {
      predicate.getExpressions()
          .add(cb.equal(root.get("applyTypeKey"), queryFlowDto.getFlowType()));
    }
    return predicate;
  }

}
