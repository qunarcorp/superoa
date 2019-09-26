package com.qunar.superoa.service.ipml;

import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.ApproveLogRepository;
import com.qunar.superoa.model.ApproveLog;
import com.qunar.superoa.service.ApproveLogServiceI;
import com.qunar.superoa.utils.DateTimeUtil;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:50 AM 2018/12/21
 * @Modify by:
 */
@Service
public class ApproveLogServiceImpl implements ApproveLogServiceI {

  @Autowired
  private ApproveLogRepository approveLogRepository;

  @Override
  public Optional<List<ApproveLog>> findByOidOrderByApproveTime(String oid) {
    return approveLogRepository.findByOidOrderByApproveTime(oid);
  }

  @Override
  public ApproveLog getApproveLogByProInstIdAndTaskId(String proinstId, String taskId) {
    return approveLogRepository.findTopByOidAndTaskIdOrderByApproveTimeDesc(proinstId, taskId);
  }

  @Override
  public ApproveLog getApproveLogByProInstIdAndTaskIdAndType(String proinstId, String taskId, String type) {
    return approveLogRepository.findTopByOidAndTaskIdAndManagerTypeOrderByApproveTimeDesc(proinstId, taskId, type);
  }

  @Override
  public ApproveLog createApproveLog(String procInstId, String taskId, String nodeDefKey, String taskName, String flowModelName,
                                     String type, String memo, String currentUserId, String currentUserName) {
    ApproveLog oldApproveLog = getApproveLogByProInstIdAndTaskId(procInstId, taskId);
    ApproveLog approveLog = new ApproveLog();
    approveLog.setTaskId(taskId);
    approveLog.setOid(procInstId);
    approveLog.setOtype(flowModelName);
    approveLog.setManagerType(type);
    approveLog.setMemo(memo);
    approveLog.setApproveTime(DateTimeUtil.getDateTime());
    approveLog.setApproveUserId(currentUserId);
    approveLog.setApproveUserName(currentUserName);
    approveLog.setNodeDefKey(nodeDefKey);
    if (oldApproveLog != null) {
      //当前task上一步审批为转交  当前节点日志设置为转交人
      if (Constant.FLOW_FORWARD.equals(oldApproveLog.getManagerType())) {
        approveLog.setTaskName(Constant.FLOW_FORWARD_USER);
      } else if (Constant.FLOW_COUNTERSIGN.equals(oldApproveLog.getManagerType())) {
        //当前task上一步审批为加签  当前节点日志设置为加签人
        approveLog.setTaskName(Constant.FLOW_COUNTERSIGN_USER);
        approveLog.setNextTaskName(oldApproveLog.getTaskName());
      } else {
        approveLog.setTaskName(taskName);
      }
    } else {
      approveLog.setTaskName(taskName);
    }
    return save(approveLog);
  }

  @Override
  public ApproveLog save(ApproveLog approveLog) {
    return approveLogRepository.save(approveLog);
  }
}
