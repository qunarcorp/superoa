package com.qunar.superoa.listener;

import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.model.FlowOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction:  订单发起人审批
 * @Date:Created in 11:04 2018/11/27
 * @Modify by:
 */
@Component
public class InitiatorListener extends CandidatesListener {

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  /**
   * 重写设置节点操作着方法
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String processInstanceId = delegateExecution.getProcessInstanceId();
    Optional<FlowOrder> flowOrder = flowOrderRepository.findByProcInstId(processInstanceId);
    List<String> candidateUsers = new ArrayList<>();
    candidateUsers.add(flowOrder.get().getApplyUserId());
    userTask.setCandidateUsers(candidateUsers);
  }

}
