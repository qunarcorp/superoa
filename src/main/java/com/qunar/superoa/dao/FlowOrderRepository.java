package com.qunar.superoa.dao;

import com.qunar.superoa.dto.FlowModelNameDto;
import com.qunar.superoa.model.FlowOrder;

import java.util.List;
import java.util.Optional;

import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by xing.zhou on 2018/8/31.
 */
public interface FlowOrderRepository extends JpaRepository<FlowOrder, String>, JpaSpecificationExecutor<FlowOrder> {

  int countByNextApproveUsersLike(String qtalk);

  int countByApprovedUsersLikeOrApprovedUsersLike(String qtalk, String qtalk1);

  int countByApplyUserIdAndApplyStatus(String qtalk, int status);

  Optional<List<FlowOrder>> findByApplyStatus(int status);

  Optional<FlowOrder> findByProcInstId(String procInstId);

  @Query(value = "select  new com.qunar.superoa.dto.FlowModelNameDto(f.applyTypeKey,f.applyTypeName) " +
      "from FlowOrder f where f.applyUserId= ?1 GROUP BY f.applyTypeKey,f.applyTypeName  ORDER BY count(f.applyTypeKey) desc ")
  List<FlowModelNameDto> findByQtalk(String qtalk);

  /**
   * 查询我已审批  有流程结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName,o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?3% or o.headline like %?3%) and (o.approvedUsers like %?1% or o.approvedUsers like %?2%) " +
      " and o.applyUserId like %?5% and o.applyTime >= ?7 and o.applyTime <= ?8 and o.lastApproveTime >= ?9 and o.lastApproveTime <= ?10 " +
      " and o.applyTypeKey like %?4% and o.applyStatus in (?6)  order by o.applyTime desc")
  Page<FlowOrder> findMyApprovedFlowOrderTime(String approvedUser, String agentUser, String keys, String typeKey, String startUser, List<Integer> status,
                                              String applyBeginTime, String applyEndTime, String approveBeginTime, String approveEndTime, Pageable pageable);

  /**
   * 查询我已审批  无流程结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?3% or o.headline like %?3%) and (o.approvedUsers like %?1% or o.approvedUsers like %?2%) " +
      " and o.applyUserId like %?5% and o.applyTime >= ?7 and o.applyTime <= ?8 " +
      " and o.applyTypeKey like %?4% and o.applyStatus in (?6)  order by o.applyTime desc")
  Page<FlowOrder> findMyApprovedFlowOrder(String approvedUser, String agentUser, String keys, String typeKey, String startUser, List<Integer> status,
                                          String applyBeginTime, String applyEndTime, Pageable pageable);

  /**
   * 查询我发起的  有结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?2% or o.headline like %?2%) and o.applyUserId = ?1 " +
      " and o.applyTime >= ?5 and o.applyTime <= ?6 and o.lastApproveTime >= ?7 and o.lastApproveTime <= ?8 " +
      " and o.applyTypeKey like %?3% and o.applyStatus in (?4) " +
      " order by o.applyTime desc")
  Page<FlowOrder> findMyStartFlowOrderTime(String startUser, String keys, String typeKey, List<Integer> status, String applyBeginTime, String applyEndTime,
                                           String approveBeginTime, String approveEndTime, Pageable pageable);

  /**
   * 查询我发起的  无结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?2% or o.headline like %?2%) and o.applyUserId = ?1 " +
      " and o.applyTime >= ?5 and o.applyTime <= ?6 " +
      " and o.applyTypeKey like %?3% and o.applyStatus in (?4) " +
      " order by o.applyTime desc")
  Page<FlowOrder> findMyStartFlowOrder(String startUser, String keys, String typeKey, List<Integer> status,
                                       String applyBeginTime, String applyEndTime, Pageable pageable);

  /**
   * 查询知会给我的  有结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?2% or o.headline like %?2%) and o.notifyUsers like %?4% " +
      " and o.applyTime >= ?5 and o.applyTime <= ?6 and o.lastApproveTime >= ?7 and o.lastApproveTime <= ?8 " +
      " and o.applyTypeKey like %?3% and o.applyUserId like %?1% " +
      " order by o.applyTime desc")
  Page<FlowOrder> findNotifyMeFlowOrderTime(String startUser, String keys, String typeKey, String notifyUser, String applyBeginTime, String applyEndTime,
                                            String approveBeginTime, String approveEndTime, Pageable pageable);

  /**
   * 查询知会给我的  有结束时间
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?2% or o.headline like %?2%) and o.notifyUsers like %?4% " +
      " and o.applyTime >= ?5 and o.applyTime <= ?6 " +
      " and o.applyTypeKey like %?3% and o.applyUserId like %?1% " +
      " order by o.applyTime desc")
  Page<FlowOrder> findNotifyMeFlowOrder(String startUser, String keys, String typeKey, String notifyUser, String applyBeginTime, String applyEndTime, Pageable pageable);


  /**
   * 查询待我审批的
   *
   * @return
   */
  @Query(value = "SELECT new com.qunar.superoa.model.FlowOrder (o.id,o.applyUserId,o.applyUserDept,o.applyUserName," +
      "o.headline,o.applyTypeKey,o.applyTypeName,o.applyTime,o.lastApproveTime," +
      "o.lastApproveUserId,o.nextApproveUsers,o.approvedUsers,o.applyStatus,o.procInstId,o.logOid,o.updateTime) " +
      " FROM FlowOrder o LEFT JOIN com.qunar.superoa.model.FormData d ON o.procInstId = d.procInstId " +
      " where (d.formDatas like %?1% or o.headline like %?1%) and o.applyStatus = 1 " +
      " and (o.nextApproveUsers like %?4% " +
      //查询被代理人和被代理流程
      " or (o.nextApproveUsers like %?5% and o.applyTypeKey in (?17)) or (o.nextApproveUsers like %?6% and o.applyTypeKey in (?18))" +
      " or (o.nextApproveUsers like %?7% and o.applyTypeKey in (?19)) or (o.nextApproveUsers like %?8% and o.applyTypeKey in (?20))" +
      " or (o.nextApproveUsers like %?9% and o.applyTypeKey in (?21)) or (o.nextApproveUsers like %?10% and o.applyTypeKey in (?22)) " +
      " or (o.nextApproveUsers like %?11% and o.applyTypeKey in (?23)) or (o.nextApproveUsers like %?12% and o.applyTypeKey in (?24))" +
      " or (o.nextApproveUsers like %?13% and o.applyTypeKey in (?25)) or (o.nextApproveUsers like %?14% and o.applyTypeKey in (?26)))" +
      " and o.applyTypeKey like %?3% " +
      " and o.applyTime >= ?15 and o.applyTime <= ?16 " +
      " and o.applyUserId like %?2% " +
      " order by o.updateTime desc")
  Page<FlowOrder> findNeedMyApproveFlowOrder(String keys, String startUser, String typeKey, String user1, String user2, String user3,
                                             String user4, String user5, String user6, String user7, String user8, String user9,
                                             String user10, String user11, String applyBeginTime, String applyEndTime,
                                             List<String> keys1, List<String> keys2, List<String> keys3, List<String> keys4, List<String> keys5,
                                             List<String> keys6, List<String> keys7, List<String> keys8, List<String> keys9, List<String> keys10, Pageable pageable);
}
