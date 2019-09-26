package com.qunar.superoa.dao;

import com.qunar.superoa.model.ExtSysFlowModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:38 PM 2019/4/29
 * @Modify by:
 */
public interface ExtSysFlowModelRepository extends JpaRepository<ExtSysFlowModel, String>, JpaSpecificationExecutor<ExtSysFlowModel> {

  /**
   * 查询当前用户可发起流程所涉及到的所有外部系统标识
   */
  @Query(value = "select distinct processKeys from ExtSysFlowModel")
  List<String> findAllProcessKeys();

  /**
   * 根据原系统id和系统标识查询可发起流程
   */
  List<ExtSysFlowModel> findByOidAndAndProcessKeys(String oid, String processKeys);

}
