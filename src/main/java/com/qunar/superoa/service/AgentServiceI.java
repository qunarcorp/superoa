package com.qunar.superoa.service;

import com.qunar.superoa.dto.AgentDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.model.Agent;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/21_上午10:49
 * @Despriction: 代理人相关接口
 */

public interface AgentServiceI {

  /**
   * 根据搜索内容 返回分页列表
   *
   * @param pageAble 分页信息和关键字信息
   * @return 返回PageResult结果集
   */
  PageResult<AgentDto> getListPageAble(PageAble pageAble);

  /**
   * 根据被代理人查询代理人
   * @param qtalk
   * @return
   */
  List<Agent> getAgentByQtalk(String qtalk);

  /**
   * 根据代理人查询被代理人
   * @param agent
   * @return
   */
  Optional<List<Agent>> getAgentByAgent(String agent);

}