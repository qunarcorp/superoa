package com.qunar.superoa.listener;

import com.qunar.superoa.constants.Constant;
import java.util.ArrayList;
import java.util.List;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 17:16 2018/11/13
 * @Modify by:
 *
 *  ${deptDirectorListener}
 */
@Component
public class DeptDirectorListener extends CandidatesListener {

  /**
   * 查询部门主管
   * @param userTask
   * @param delegateExecution
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String owner = delegateExecution.getVariable("owner").toString();
    List<String> candidateUsers = new ArrayList<String>();
    candidateUsers.add(userInfoUtil.getUserDeptLeader(owner, Constant.DEPT_DIRECTOR));
    userTask.setCandidateUsers(candidateUsers);
  }

}
