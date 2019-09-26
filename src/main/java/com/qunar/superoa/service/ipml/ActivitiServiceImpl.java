package com.qunar.superoa.service.ipml;

import com.google.common.collect.Maps;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.ApproveLog;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ActivitiServiceI;
import com.qunar.superoa.service.ApproveLogServiceI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xing.zhou on 2018/8/20.
 */

@Service
public class ActivitiServiceImpl implements ActivitiServiceI {

  private static final Logger logger = LoggerFactory.getLogger(ActivitiServiceImpl.class);

  @Autowired
  protected RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  protected TaskService taskService;

  @Autowired
  protected HistoryService historyService;

  @Autowired
  protected ProcessEngineFactoryBean processEngineFactoryBean;

  @Autowired
  private ApproveLogServiceI approveLogServiceI;

  /**
   * 发起流程实例
   */
  @Override
  public String startProcessInstance(String key, String keys, String values, String currentUserId) {
    Map<String, Object> reasonMap = getReasonMap(keys, values, currentUserId);
    //获取流程实例对象
    ProcessInstance processInstance = null;
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().active()
        .processDefinitionKey(key).singleResult();
    if (processDefinition == null) {
      processInstance = runtimeService.startProcessInstanceByKey("demo", reasonMap);
    } else {
      processInstance = runtimeService.startProcessInstanceByKey(key, reasonMap);
    }
    //流程实例ID
    logger.info("流程实例ID：{}  流程定义ID：{}" + processInstance.getId(), processInstance.getProcessDefinitionId());
    return processInstance.getId();
  }

  private Map<String, Object> getReasonMap(String keys, String values, String currentUserId) {
    Map<String, Object> reasonMap = new HashMap<String, Object>();
    if (keys != null && !"".equals(keys)) {
      String[] keyList = keys.split(",");
      String[] valueList = values.split(",");
      for (int i = 0; i < keyList.length; i++) {
        if (keyList[i] != null && !"".equals(keyList[i])) {
          String value = "";
          if(i < valueList.length){
            value = valueList[i].trim();
          }
          if ("true".equals(value) || "false".equals(value)) {
            reasonMap.put(keyList[i], Boolean.valueOf(value));
          } else if (NumberUtils.isNumber(value)) {
            reasonMap.put(keyList[i], Double.parseDouble(value));
          } else if ("是".equals(value)) {
            reasonMap.put(keyList[i], true);
          } else if ("否".equals(value)) {
            reasonMap.put(keyList[i], false);
          } else {
            reasonMap.put(keyList[i], value);
          }
        }
      }
    }
    reasonMap.put("owner", currentUserId);
    return reasonMap;
  }

  @Override
  public void changeVariablesByExecutionId(String executionId, String keys, String values, String currentUserId) {
    Map<String, Object> reasonMap = getReasonMap(keys, values, currentUserId);
    runtimeService.setVariables(executionId, reasonMap);
  }

  /**
   * 获取所有流程实例
   */
  @Override
  public List<Map<String, Object>> getFlowByDeploymentId(String deploymentId) {
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
        .deploymentId(deploymentId).singleResult();

//        return runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinition.getId()).list();
    List<Task> list = taskService.createTaskQuery().processDefinitionId(processDefinition.getId())
        .list();
    List<Map<String, Object>> mapList = new ArrayList<>();
    for (Task task : list) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("taskId", task.getId());
      map.put("taskName", task.getName());
      map.put("processInstanceId", task.getProcessInstanceId());
      map.put("processDefinitionId", task.getProcessDefinitionId());
      map.put("createTime", task.getCreateTime());
      map.put("taskDefinitionKey", task.getTaskDefinitionKey());
      map.put("parentTaskId", task.getParentTaskId());
      map.put("assignee", task.getAssignee());
      mapList.add(map);
    }
    return mapList;
  }

  /**
   * 同意流程
   */
  @Override
  public boolean completeByTaskId(String taskId) {
//    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    taskService.complete(taskId);//完成taskId对应的任务
    //TODO 添加log
//        completeUserIsNull(taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult());
    return true;
  }

  //无审批人的节点  自动通过
  public void completeUserIsNull(Task task) {
    if (task == null) {
      return;
    }
    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
    if (identityLinks == null || identityLinks.size() == 0) {
      completeByTaskId(task.getId());
    }
  }

  /**
   * 撤销流程
   */
  @Override
  public String revokeProcessInstance(String processInstanceId, String reason) {
    Map<String, Object> reasonMap = Maps.newHashMap();
    reasonMap.put("reason", reason);
    //撤销流程
    try {
      runtimeService.deleteProcessInstance(processInstanceId, reason);
    } catch (Exception e) {
      logger.error("撤销流程失败",e);
      throw new FlowException(ResultEnum.ACTIVITI_FLOW_NOT);
    }
    return "已完成";
  }

  /**
   * 拒绝流程
   */
  @Override
  public String rejectTask(String taskId, String reason) {
    Map<String, Object> reasonMap = new HashMap<String, Object>();
    reasonMap.put("reason", reason);
    //拒绝taskId对应的任务
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    runtimeService.deleteProcessInstance(task.getProcessInstanceId(), reason);
    //TODO 添加log
    return "已完成";
  }

  /**
   * 回退流程
   */
  @Override
  public String backTask(String taskId, String reason) {
    Map<String, Object> reasonMap = new HashMap<String, Object>();
    reasonMap.put("reason", reason);
    taskService.complete(taskId, reasonMap);//拒绝taskId对应的任务
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), reason);
    //TODO 添加log
    return "已完成";
  }

  /**
   * 获取指定用户的流程
   */
  @Override
  public List<Map<String, Object>> getTasksByUserId(String userId) {
    List<Task> list = taskService.createTaskQuery().taskCandidateUser(userId)
        .orderByTaskCreateTime().desc().list();//指定个人任务查询

    //TODO 查询代理人的tasks
    List<Map<String, Object>> mapList = new ArrayList<>();
    for (Task task : list) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("taskId", task.getId());
      map.put("taskName", task.getName());
      map.put("processInstanceId", task.getProcessInstanceId());
      map.put("processDefinitionId", task.getProcessDefinitionId());
      map.put("createTime", task.getCreateTime());
      map.put("taskDefinitionKey", task.getTaskDefinitionKey());
      map.put("parentTaskId", task.getParentTaskId());
      map.put("assignee", task.getAssignee());
      mapList.add(map);
    }
    return mapList;
  }

  /**
   * 获取当前登录用户的流程数
   */
  @Override
  public int getcountTask() {
    String userId = SecurityUtils.currentUsername();
    List<Task> list = taskService.createTaskQuery().taskCandidateUser(userId)
        .orderByTaskCreateTime().desc().list();//指定个人任务查询

    return list == null ? 0 : list.size();
  }

  @Override
  public void test(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
        .processDefinitionId(task.getProcessDefinitionId()).list();
    for (ProcessDefinition processDefinition : processDefinitions) {
//            processDefinition
    }

  }

  /**
   * 获取节点流程审批人
   *
   * @param taskId 任务流程id
   */
  @Override
  public String getApproveUser(String taskId) {
    StringBuffer users = new StringBuffer();
    users.append(",");
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
    if (identityLinks != null && identityLinks.size() > 0) {
      for (IdentityLink identityLink : identityLinks) {
        if (identityLink.getUserId() != null && !"".equals(identityLink.getUserId())) {
          users.append(identityLink.getUserId()).append(",");
        }
      }
    }
    return users.toString();
  }


  @Override
  public List<Task> getTaskByProcInstId(String procInstId) {
    return taskService.createTaskQuery().processInstanceId(procInstId).list();
  }


  @Override
  public Task getTaskByProcInstIdAndUser(String procInstId, String approveUser) {
    // 多个只拿一个
    List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId)
        .taskCandidateOrAssigned(approveUser).list();
    if (tasks != null && tasks.size() > 0) {
      return tasks.get(0);
    } else {
      return null;
    }
  }

  @Override
  public long getTaskCountByProcInstId(String procInstId) {
    return taskService.createTaskQuery().processInstanceId(procInstId).count();
  }

  @Override
  public String getCounterSignApproveNestUsers(String procInstId, String taskId) {
    //下一节点审批人为上一加签审批的操作人
    ApproveLog oldApproveLog = approveLogServiceI
        .getApproveLogByProInstIdAndTaskIdAndType(procInstId, taskId, Constant.FLOW_COUNTERSIGN);
    StringBuffer agentUser = new StringBuffer();
    String memo = oldApproveLog.getMemo();
    if (memo.contains("(" + oldApproveLog.getApproveUserId() + "代理")) {
      agentUser.append(memo.substring(0, memo.indexOf("处理)")).substring(memo.indexOf("代理") + 2));
    }
    //删除非加签操作人以外的审批人
    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
    if (identityLinks != null && identityLinks.size() > 0) {
      identityLinks.forEach(identityLink -> {
        if (!oldApproveLog.getApproveUserId().equals(identityLink.getUserId()) && !agentUser.toString()
            .equals(identityLink.getUserId())) {
          taskService.deleteCandidateUser(taskId, identityLink.getUserId());
        }
      });
    }
    //这里可能会获取不到日志数据导致报错
    return oldApproveLog.getApproveUserId() + ",";
  }

  @Override
  public void deleteApproveUsers(String taskId, String users, int type) {
    List<String> userList = new ArrayList<>();
    Arrays.stream(users.split(",")).forEach(user -> {
      if (StringUtils.isNotBlank(user)) {
        userList.add(user);
      }
    });
    //删除指定的审批人
    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
    if (identityLinks != null && identityLinks.size() > 0) {
      identityLinks.forEach(identityLink -> {
        if (type == 0) {
          if (StringUtils.isNotBlank(identityLink.getUserId()) && userList.contains(identityLink.getUserId())) {
            taskService.deleteCandidateUser(taskId, identityLink.getUserId());
          }
        } else if (type == 1) {
          if (StringUtils.isNotBlank(identityLink.getUserId()) && !userList.contains(identityLink.getUserId())) {
            taskService.deleteCandidateUser(taskId, identityLink.getUserId());
          }
        }
      });
    }
  }

  @Override
  public String getApproveNestUsers(String procInstId, List<Task> tasks) {
    List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
    if (taskList == null || taskList.size() == 0) {
      return "";
    } else {
      StringBuffer users = new StringBuffer();
      users.append(",");
      List<String> taskIds = new ArrayList<>();
      if (tasks != null) {
        tasks.forEach(task -> taskIds.add(task.getId()));
      }
      taskList.forEach(task -> {
        if (tasks == null || !taskIds.contains(task.getId())) {
          List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
          if (identityLinks != null && identityLinks.size() > 0) {
            identityLinks.forEach(identityLink -> {
              if (StringUtils.isNotBlank(identityLink.getUserId())) {
                users.append(identityLink.getUserId()).append(",");
              }
            });
          }
        }
      });
      return users.toString();
    }
  }


}
