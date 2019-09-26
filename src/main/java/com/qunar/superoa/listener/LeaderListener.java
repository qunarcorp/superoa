package com.qunar.superoa.listener;

import com.qunar.superoa.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * ${leaderListener} Created by xing.zhou on 2018/9/14.
 */
@Component
public class LeaderListener extends CandidatesListener {

  /**
   * 重写设置节点操作着方法   获取直属领导为当前节点审批人
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String owner = delegateExecution.getVariable("owner").toString();
    List<String> candidateUsers = new ArrayList<String>();
    candidateUsers.add(userInfoUtil.getUserTL(owner));
    userTask.setCandidateUsers(candidateUsers);
  }

  /**
   * 知会节点  知会直属TL
   */
  @Override
  public void sendNotify(ManualTask manualTask, DelegateExecution delegateExecution) {
    String owner = delegateExecution.getVariable("owner").toString();
    String notifyUsersStr = userInfoUtil.getUserTL(owner) + ",";
    String processInstanceId = delegateExecution.getProcessInstanceId();
    //知会查看人字段
    Object temp = Cache.getFlowDataCacheByKey(processInstanceId);
    Cache.setFlowDataCache(processInstanceId,
        temp != null && StringUtils.isNotBlank(temp.toString()) ? temp.toString() + notifyUsersStr : notifyUsersStr);
  }
}
