package com.qunar.superoa.listener;

import com.qunar.superoa.constants.Constant;
import java.util.ArrayList;
import java.util.List;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction:  获取部门vp为当前节点审批人
 * @Date:Created in 14:32 2018/10/18
 * @Modify by:
 */
@Component
public class DeptVPListener extends CandidatesListener {

  /**
   * 重写设置节点操作着方法
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String owner = delegateExecution.getVariable("owner").toString();
    List<String> candidateUsers = new ArrayList<String>();
    candidateUsers.add(userInfoUtil.getUserDeptLeader(owner, Constant.DEPT_VP));
    userTask.setCandidateUsers(candidateUsers);
  }
}
