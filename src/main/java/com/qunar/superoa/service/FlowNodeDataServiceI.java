package com.qunar.superoa.service;

/**
 * Created by xing.zhou on 2019/2/20.
 * 节点流程数据service
 */
public interface FlowNodeDataServiceI {

  /**
   * 保存节点数据
   * @param createUserId
   * @param createUserName
   * @param formDatas
   * @param flowKey
   * @param flowName
   * @param nodeDefKey
   * @param nodeName
   * @param procInstId
   */
  void saveFlowNodeData(String createUserId, String createUserName, String formDatas, String flowKey, String flowName,
                        String nodeDefKey, String nodeName, String procInstId);

}
