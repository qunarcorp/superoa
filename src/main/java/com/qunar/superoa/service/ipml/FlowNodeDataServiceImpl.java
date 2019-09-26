package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.FlowNodeDataRepository;
import com.qunar.superoa.model.FlowNodeData;
import com.qunar.superoa.service.FlowNodeDataServiceI;
import com.qunar.superoa.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhouxing
 * @date 2019-02-20 11:04
 */
@Service
@Slf4j
public class FlowNodeDataServiceImpl implements FlowNodeDataServiceI {
  @Autowired
  public FlowNodeDataRepository flowNodeDataRepository;

  @Override
  public void saveFlowNodeData(String createUserId, String createUserName, String formDatas, String flowKey, String flowName,
                               String nodeDefKey, String nodeName, String procInstId) {
    FlowNodeData flowNodeData = new FlowNodeData();
    flowNodeData.setCreateTime(DateTimeUtil.getDateTime());
    flowNodeData.setCreateUserId(createUserId);
    flowNodeData.setCreateUserName(createUserName);
    flowNodeData.setFormDatas(formDatas);
    flowNodeData.setFormModelFlowKey(flowKey);
    flowNodeData.setFormModelName(flowName);
    flowNodeData.setNodeDefKey(nodeDefKey);
    flowNodeData.setNodeName(nodeName);
    flowNodeData.setProcInstId(procInstId);
    flowNodeDataRepository.save(flowNodeData);
  }
}
