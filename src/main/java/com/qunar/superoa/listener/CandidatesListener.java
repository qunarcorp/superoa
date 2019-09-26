package com.qunar.superoa.listener;

import com.qunar.superoa.cache.Cache;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.FormDataRepository;
import com.qunar.superoa.dao.GroupRepository;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.FormData;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.JsonMapUtil;
import com.qunar.superoa.utils.UserInfoUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ${candidatesListener} Created by xing.zhou on 2018/8/21.
 */
@Slf4j
@Component
public class CandidatesListener implements ExecutionListener {

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  protected UserInfoUtil userInfoUtil;

  @Autowired
  private FormDataRepository formDataRepository;

  @Override
  public void notify(DelegateExecution delegateExecution) {
    if (delegateExecution.getCurrentFlowElement().getClass().equals(UserTask.class)) {
      UserTask userTask = (UserTask) delegateExecution.getCurrentFlowElement();
      setGroupApproveUser(userTask, delegateExecution);

    } else if (delegateExecution.getCurrentFlowElement().getClass().equals(ManualTask.class)) {
      ManualTask manualTask = (ManualTask) delegateExecution.getCurrentFlowElement();
      sendNotify(manualTask, delegateExecution);
    }
  }

  /**
   * 设置操作人  获取当前节点指定的工作组group人员为当前节点审批人
   */
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    List<String> groups = userTask.getCandidateGroups();
    List<String> candidateUsers = new ArrayList<String>();
    if (groups != null && groups.size() > 0) {
      groups.forEach(group -> groupRepository.findByName(group.trim())
          .ifPresent(workGroup -> Arrays.stream(workGroup.getMembers().split(",")).forEach(user -> {
            if (!user.isEmpty()) {
              candidateUsers.add(user);
            }
          })));
      if (candidateUsers.size() == 0) {
        throw new FlowException(ResultEnum.FLOW_APPROVE_USERS_IS_NULL);
      }
      userTask.setCandidateUsers(candidateUsers);
    }
  }

  /**
   * 知会节点  知会组内所有人
   */
  public void sendNotify(ManualTask manualTask, DelegateExecution delegateExecution) {
    String[] group = manualTask.getDocumentation().split("_");
    List<String> notifyUsers = new ArrayList();
    String processInstanceId = delegateExecution.getProcessInstanceId();
    if ("G".equals(group[0])) {
      groupRepository.findByName(group[1])
          .ifPresent(workGroup -> Arrays.stream(workGroup.getMembers().split(",")).forEach(user -> {
            if (!user.isEmpty()) {
              notifyUsers.add(user);
            }
          }));
    } else if ("U".equals(group[0])) {
      notifyUsers.add(group[1]);
    } else if ("D".equals(group[0])) {
      FormData formData = formDataRepository.findByProcInstId(processInstanceId);
      String userNames;
      if (formData == null) {
        String uuid = delegateExecution.getVariable(Constant.ACTIVITI_VARIABLE_KEY).toString();
        userNames = JsonMapUtil
            .getValueByKey(group[1], (Map<String, Object>) Cache.getFlowDataCacheByKey(uuid));
      } else {
        userNames = JsonMapUtil
            .getValueByKey(group[1], CommonUtil.s2m(formData.getFormDatas()));
      }
      notifyUsers.add(userNames);
    }
    StringBuffer notifyUsersStr = new StringBuffer();
    notifyUsersStr.append(",");
    for (String notifyUser : notifyUsers) {
      notifyUsersStr.append(notifyUser).append(",");
    }
    //知会查看人字段
    Object temp = Cache.getFlowDataCacheByKey(processInstanceId);
    Cache.setFlowDataCache(processInstanceId,
        temp != null && StringUtils.isNotBlank(temp.toString()) ? temp.toString() + notifyUsersStr.toString() : notifyUsersStr.toString());

  }

}
