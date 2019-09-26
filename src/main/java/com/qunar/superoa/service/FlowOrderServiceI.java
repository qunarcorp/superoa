package com.qunar.superoa.service;

import com.qunar.superoa.dto.ApproveCountDto;
import com.qunar.superoa.dto.FlowCountDto;
import com.qunar.superoa.dto.FlowDataDto;
import com.qunar.superoa.dto.FlowOrderDto;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.QueryFlowDto;
import com.qunar.superoa.model.FlowOrder;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 11:12 2018/12/21
 * @Modify by:
 */
public interface FlowOrderServiceI {

  /**
   * 获取当前用户草稿、审批中、已完结流程数
   * @return
   */
  FlowCountDto getFlowCount();

  /**
   * 获取我的代办、已办流程数(当前登录人)
   * @return
   */
  ApproveCountDto getApproveCount();

  /**
   * 获取我的代办流程数(当前登录人)
   * @param qtalk
   * @return
   */
  int getToApproveCount(String qtalk);

  /**
   * 根据flowId获取流程实例
   */
  FlowDataDto getFlowById(String flowId);

  /**
   * 移动端查询具体流程信息
   * @param flowId
   * @param currentUserId
   * @return
   */
  FlowDataDto getFlowById(String flowId, String currentUserId);

  /**
   * 获取我发起、待我审批、我已审批的流程
   */
  PageResult<FlowOrderDto> getMyFlows(QueryFlowDto queryFlowDto);

  /**
   *
   * @param status
   * @return
   */
  Optional<List<FlowOrder>> findByApplyStatus(int status);

  /**
   * 查询flowOrder
   * @param flowId
   * @return
   */
  FlowOrder getFlowOrderById(String flowId);

  /**
   *
   * @param flowOrder
   * @return
   */
  FlowOrder save(FlowOrder flowOrder);

  /**
   * 审批完成后修改flowOrder信息
   * @param flowOrder
   * @param nextUsers 下一节点审批人（全部节点）
   * @param nestApproveNotifyUsers 下一节点审批人(新加节点)
   * @param currentUserId 当前节点审批
   * @param agentUser 被代理人
   * @param notifyUsers 知会人
   * @param taskNum 当前task数
   * @return
   */
  FlowOrder approveUpdateFlowOrder(FlowOrder flowOrder, String nextUsers, String nestApproveNotifyUsers, String currentUserId, String agentUser,
      String notifyUsers, long taskNum, String type);

}
