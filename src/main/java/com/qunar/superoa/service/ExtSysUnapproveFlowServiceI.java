package com.qunar.superoa.service;

import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.QueryExtSysUnapproveDto;
import com.qunar.superoa.model.ExtSysUnapproveFlow;
import java.util.List;

/**
 * @Auther: chengyan.liang
 * @Despriction: 集成系统待审批流程service接口层
 * @Date:Created in 4:21 PM 2019/4/26
 * @Modify by:
 */
public interface ExtSysUnapproveFlowServiceI {

  /**
   * 更新一条待审批流程
   * @param dataSource 集成系统所发送数据
   */
  Object updateExtSysUnapproveFlow(Object dataSource);

  /**
   * 删除一条待审批流程
   * @param dataSource 集成系统所发送数据
   */
  Object deleteExtSysUnapproveFlow(Object dataSource);

  /**
   * 批量更新审批流程
   * @param dataSource 集成系统所发送数据
   */
  Object updatePatchExtSysUnapproveFlow(Object dataSource);

  /**
   * 获取外部系统待我审批的、我发起的进行中流程
   */
  PageResult<ExtSysUnapproveFlow> getMyExtSysUnapproveFlows(QueryExtSysUnapproveDto queryUnapproveDto);

  /**
   * 获取外部系统我的待办流程数(当前登录人)
   */
  int getExtSysToApproveCount();

  /**
   * 获取外部系统当前用户发起的审批中流程数
   */
  int getMyExtSysFlowCount();

  /**
   * 获取当前用户待审批流程所涉及到的所有外部系统标识
   */
  List<String> getMyExtSysUnapproveProcessKeys();
}
