package com.qunar.superoa.service;

import com.qunar.superoa.dto.ExternalQueryDto;
import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.model.FlowOrder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.activiti.engine.task.Task;

/**
 * Created by xing.zhou on 2018/8/31.
 */
public interface FlowServiceI {

  /**
   * 发起流程
   */
  String startProcessInstance(FormDataDto formDataDto, String type);

  /**
   * 外部系统调用 发起流程
   * @param externalQueryDto
   * @return
   */
  String startFromExternal(ExternalQueryDto externalQueryDto, String ip);

  /**
   * 存草稿流程
   */
  String notStartProcessInstance(FormDataDto formDataDto);

  /**
   * 发起草稿流程
   */
  String startDraftFlow(FormDataDto formDataDto);

  /**
   * 撤销流程
   */
  String revokeProcessInstance(String flowId, String reason);

  /**
   * 获取发起列表、待审批列表、已审批列表的流程
   */
  Optional<List<FlowOrder>> getTodos(int queryType);

  /**
   * 查询当前单子是否在可编辑节点上
   */
  Boolean inExitNode(FlowOrder flowOrder);

  /**
   * 审批同意流程
   */
  String consentFlowById(String flowId, String memo, String formDatas);

  /**
   * 审批同意流程
   */
  String consentFlowById(String flowId, String memo, String qtalk, String userName,
      Boolean fromApp, Task nowTask, String formDatas);

  /**
   * 转交审批流程
   *
   * @param flowId 流程实例id
   * @param userId 被转交人qtalk
   * @param memo 审批意见
   */
  String forwardFlowById(String flowId, String userId, String memo, String formDatas);

  /**
   * 转交审批流程 移动端
   *
   * @param flowId 流程实例id
   * @param userId 被转交人qtalk
   * @param memo 审批意见
   */
  String forwardFlowById(String flowId, String userId, String memo, String currentUserId,
      String currentUsername, Boolean fromApp, String formDatas);

  /**
   * 加签审批流程
   *
   * @param flowId 流程实例id
   * @param userId 加签人qtalk
   * @param memo 审批意见
   */
  String counterSignFlowById(String flowId, String userId, String memo, String formDatas);

  /**
   * 加签审批流程 移动端
   *
   * @param flowId 流程实例id
   * @param userId 加签人qtalk
   * @param memo 审批意见
   */
  String counterSignFlowById(String flowId, String userId, String memo, String currentUserId,
      String currentUsername, Boolean fromApp, String formDatas);

  /**
   * 审批拒绝流程
   */
  String rejectFlowById(String flowId, String memo, String formDatas);

  /**
   * 审批拒绝流程
   */
  String rejectFlowById(String flowId, String memo, String qtalk, String username, Boolean fromApp, String formDatas);

  /**
   * 催办
   */
  String notifyNextApproveUsers(String flowId) throws Exception;

  /**
   * 获取当前流程节点流程跟踪图
   */
  String getFlowTraceImage(String flowId) throws Exception;

  /**
   * 移动端 根据flowId获取流程实例 k:v
   */
  List<Map<String, String>> getFlowByIdFromApp(String flowId);

  /**
   * 修改节点待审批人
   * @param userId 被替换审批人
   * @param userIds 替换的审批人，可以多个，用英文逗号隔开
   */
  String updateNodeApproveUsers(String flowId, String userId, String userIds, String memo, String operate);
}
