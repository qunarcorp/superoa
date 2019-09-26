package com.qunar.superoa.dao;

import com.qunar.superoa.model.FlowNodeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zhouxing
 * @date 2019-02-20 11:06
 */
public interface FlowNodeDataRepository extends JpaRepository<FlowNodeData, String>, JpaSpecificationExecutor<FlowNodeData> {


}
