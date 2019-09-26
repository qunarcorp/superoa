package com.qunar.superoa.service;

import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by xing.zhou on 2018/8/20.
 */
public interface ActivitiServiceI {

    /**
     * 发起流程
     * @param key
     * @param keys
     * @param values
     * @return
     */
    String startProcessInstance(String key, String keys, String values, String currentUserId);

    String revokeProcessInstance(String flowId, String reason);

    boolean completeByTaskId(String taskId);

    String rejectTask(String taskId, String reason);

    String backTask(String taskId, String reason);

    List<Map<String, Object>> getTasksByUserId(String userId);

    int getcountTask();

    List<Map<String, Object>> getFlowByDeploymentId(String deploymentId);

    void test(String taskId);

    String getApproveUser(String taskId);

    /**
     * 根据ID获取流程实例list
     * @param procInstId id
     * @return task
     */
    List<Task> getTaskByProcInstId(String procInstId);

    /**
     * 根据procInstId 和 user获取task
     * @param procInstId
     * @param approveUser
     * @return
     */
    Task getTaskByProcInstIdAndUser(String procInstId, String approveUser);

    /**
     * 根据procinstId获取当前进行中节点数
     * @param procInstId
     * @return
     */
    long getTaskCountByProcInstId(String procInstId);

    /**
     * 获取加签后同意 下一节点审批人（为当前节点加签操作人）
     * @param taskId
     * @return
     */
    String getCounterSignApproveNestUsers(String procInstId, String taskId);

    /**
     * 删除指定task上的指定审批人
     * @param taskId
     * @param users
     * @param type 0:删除users;1:删除除users外的全部审批人
     * @return
     */
    void deleteApproveUsers(String taskId, String users, int type);

    /**
     * 获取所有节点审批  去掉指定节点
     * @param tasks
     * @return
     */
    String getApproveNestUsers(String procInstId, List<Task> tasks);

    /**
     * 根据ExecutionId  修改参数
     * @param executionId
     * @param keys
     * @param values
     * @param currentUserId
     */
    void changeVariablesByExecutionId(String executionId, String keys, String values, String currentUserId);
}
