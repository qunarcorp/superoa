package com.qunar.superoa.service;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:43 AM 2018/12/21
 * @Modify by:
 */

import com.qunar.superoa.model.ApproveLog;
import java.util.List;
import java.util.Optional;

/**
 * 日志服务
 */
public interface ApproveLogServiceI {

  /**
   * 根据oid查询审批日志
   * @param oid
   * @return
   */
  Optional<List<ApproveLog>> findByOidOrderByApproveTime(String oid);

  /**
   * 根据流程实例id和任务id获取审批日志
   *
   * @param proinstId 流程实例id
   * @param taskId 任务id
   */
  ApproveLog getApproveLogByProInstIdAndTaskId(String proinstId, String taskId);

  /**
   * 根据流程实例id和任务id和审批类型获取审批日志
   * @param proinstId
   * @param taskId
   * @param type
   * @return
   */
  ApproveLog getApproveLogByProInstIdAndTaskIdAndType(String proinstId, String taskId, String type);

  /**
   * 创建日志并保存
   * @param procInstId
   * @param taskId
   * @param nodeDefKey
   * @param taskName
   * @param flowModelName
   * @param type
   * @param Memo
   * @param currentUserId
   * @param currentUserName
   * @return
   */
  ApproveLog createApproveLog(String procInstId, String taskId, String nodeDefKey, String taskName, String flowModelName,
      String type, String Memo, String currentUserId, String currentUserName);

  /**
   * 保存
   * @param approveLog
   * @return
   */
  ApproveLog save(ApproveLog approveLog);
}
