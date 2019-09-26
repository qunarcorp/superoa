package com.qunar.superoa.service.ipml;

import static org.junit.Assert.*;

import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.model.FlowOrder;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 4:50 PM 2018/12/24
 * @Modify by:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class FlowServiceImplTest {

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  @Autowired
  private RepositoryService repositoryService;

  @Test
  public void editNode() {

    FlowOrder flowOrder = flowOrderRepository.findById("e4d008a167df45910167df46a30d0001").get();
    Task task = taskService.createTaskQuery().processInstanceId(flowOrder.getProcInstId())
        .singleResult();
    String editNodeName = String
        .valueOf(runtimeService.getVariable(task.getExecutionId(), "editNodeName"));
    log.info("测试节点名称:  {}", editNodeName);

  }

}