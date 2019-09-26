package com.qunar.superoa.service;

import com.qunar.superoa.model.ExtSysFlowModel;
import java.util.List;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:42 PM 2019/4/29
 * @Modify by:
 */
public interface ExtSysFlowModelServiceI {

  /**
   * 更新一条流程
   * @param dataSource 集成系统所发送数据
   */
  Object updateExtSysFlowModel(Object dataSource);

  /**
   * 删除一条流程
   * @param dataSource 集成系统所发送数据
   */
  Object deleteExtSysFlowModel(Object dataSource);

  /**
   * 批量更新流程
   * @param dataSource 集成系统所发送数据
   */
  Object updatePatchExtSysFlowModel(Object dataSource);


  /**
   * 获取当前用户可发起流程所涉及到的所有外部系统标识
   */
  List<String> getExtSysFlowModelProcessKeys();

  /**
   * 获取所有集成系统可发起流程
   */
  List<ExtSysFlowModel> getAllExtSysFlowModel();
}
