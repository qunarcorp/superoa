package com.qunar.superoa.dao;

import com.qunar.superoa.model.ExtSysUnapproveFlow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @Auther: chengyan.liang
 * @Despriction: 集成系统待审批流程数据库操作层
 * @Date:Created in 4:02 PM 2019/4/26
 * @Modify by:
 */
public interface ExtSysUnapproveFlowRepository extends JpaRepository<ExtSysUnapproveFlow, String>, JpaSpecificationExecutor<ExtSysUnapproveFlow> {

  /**
   * 根据原系统id和系统标识查询待审批流程
   */
  List<ExtSysUnapproveFlow> findByOidAndAndProcessKeysOrderByUpdateTimeDesc(String oid, String processKeys);

  /**
   * 查询当前用户申请的进行中的流程数
   */
  int countByApplyUserLike(String userId);

  /**
   * 查询用户当前待审批的流程数
   */
  int countByApproveUsersLike(String userId);

  /**
   * 查询当前用户待审批流程所涉及到的所有外部系统标识
   */
  @Query(value = "select distinct processKeys from ExtSysUnapproveFlow where approveUsers like %?1%")
  List<String> findAllProcessKeys(String userId);

}
