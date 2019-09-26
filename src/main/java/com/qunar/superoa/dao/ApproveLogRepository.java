package com.qunar.superoa.dao;

import com.qunar.superoa.model.ApproveLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by xing.zhou on 2018/8/31.
 */
public interface ApproveLogRepository extends JpaRepository<ApproveLog, String> {

    /**
     * 根据oid查询审批日志
     * @param oid
     * @return
     */
    Optional<List<ApproveLog>> findByOidOrderByApproveTime(String oid);

    ApproveLog findTopByOidOrderByApproveTimeDesc(String oid);

    /**
     * 根据流程实例ID和TaskID获取最后一条日志
     */
    ApproveLog findTopByOidAndTaskIdOrderByApproveTimeDesc(String oid, String taskId);

    /**
     * 根据流程实例ID和TaskID和managerType获取最后一条日志
     */
    ApproveLog findTopByOidAndTaskIdAndManagerTypeOrderByApproveTimeDesc(String oid, String taskId, String type);

}
