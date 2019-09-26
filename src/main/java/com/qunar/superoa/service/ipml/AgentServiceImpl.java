package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.AgentRepository;
import com.qunar.superoa.dto.AgentDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.FlowModelNameDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.model.Agent;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.AgentServiceI;
import com.qunar.superoa.service.FlowModelServiceI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Auther: lee.guo
 * @Auther: xing.zhou
 * @Date:Created in 2018/9/21_上午10:58
 * @Despriction: 代理人
 */

@Service
public class AgentServiceImpl implements AgentServiceI {

  @Autowired
  private AgentRepository agentRepository;

  @Autowired
  private FlowModelServiceI flowModelServiceI;

  @Autowired
  public AgentServiceImpl(AgentRepository agentRepository) {
    this.agentRepository = agentRepository;
  }

  @Override
  public PageResult<AgentDto> getListPageAble(PageAble pageAble) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    List<AgentDto> agentDtos = new ArrayList<>();
    // 分页查询数据
    Page<Agent> pageList = agentRepository
        .findAll((Specification<Agent>) (root, query, criteriaBuilder) -> {
          query.where(criteriaBuilder
              .or(criteriaBuilder.like(root.get("qtalk"), "%" + pageAble.getK() + "%")));
          query.where(criteriaBuilder
              .or(criteriaBuilder.like(root.get("agent"), "%" + pageAble.getK() + "%")));
          return null;
        }, pageAble.getPageAble());

    // 对流程模板的k v 赋值
    pageList.getContent().forEach(agent -> {
      List<FlowModelNameDto> flowModels = new ArrayList<>();
      if (!StringUtils.isEmpty(agent.getProcessID())) {
        Arrays.stream(agent.getProcessID().split(",")).forEach(key -> {
          if ("all".equalsIgnoreCase(key)) {
            FlowModelNameDto flowModel = new FlowModelNameDto();
            flowModel.setFormKey("all");
            flowModel.setFormName("全部流程");
            flowModels.add(flowModel);
          } else if (!StringUtils.isEmpty(key)) {
            FlowModelNameDto flowModel = new FlowModelNameDto();
            FlowModelDto flowModelDto = flowModelServiceI.getFlowModelDtoByFormKey(key);
            flowModel.setFormKey(key);
            flowModel.setFormName(flowModelDto == null ? "已删除的流程" : flowModelDto.getFormName());
            flowModels.add(flowModel);
          }
        });
      }
      agentDtos.add(
          new AgentDto(agent.getId(), agent.getQtalk(), agent.getAgent(), agent.getDeadline(),
              agent.getProcessID(), agent.getUpdateTime(), agent.getRemarks(), flowModels));
    });

    // 返回分页对象
    return new PageResult<>(pageList, agentDtos);
  }

  @Override
  public List<Agent> getAgentByQtalk(String qtalk) {
    return agentRepository.findByDeadlineAfterAndQtalk(new Date(), qtalk);
  }

  @Override
  public Optional<List<Agent>> getAgentByAgent(String agent) {
    return agentRepository.findByDeadlineAfterAndAgent(new Date(), agent);
  }
}
