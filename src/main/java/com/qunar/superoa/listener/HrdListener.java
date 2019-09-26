package com.qunar.superoa.listener;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 4:43 PM 2018/12/11
 * @Modify by:
 */

import com.google.common.collect.Lists;
import com.qunar.superoa.constants.Constant;
import java.util.Arrays;
import java.util.List;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 获取流程发起人的人力资源总监Hrd
 */
@Component
public class HrdListener extends CandidatesListener {

  /**
   * 重写设置节点审批人方法
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String owner = delegateExecution.getVariable("owner").toString();
    List<String> candidatesUsers = Lists.newArrayList();
    String hrds = userInfoUtil.getUserDeptLeader(owner, Constant.DEPT_HRD);
    Arrays.stream(hrds.split(",")).forEach(hrd -> {
      if (StringUtils.isNotBlank(hrd)) {
        candidatesUsers.add(hrd);
      }
    });

    userTask.setCandidateUsers(candidatesUsers);
  }
}
