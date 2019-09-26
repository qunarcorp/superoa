package com.qunar.superoa.dao;

import com.qunar.superoa.model.Agent;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_下午12:14
 * @Despriction:
 */
public interface AgentRepository extends JpaRepository<Agent, String> {

  /**
   * 根据条件分页消除
   * @param agentSpecification 条件
   * @param pageAble 分页
   * @return PAGE对象结果
   */
  Page<Agent> findAll(Specification<Agent> agentSpecification, Pageable pageAble);

  /**
   * 根据被代理人查询代理人
   * @param qtalk
   * @return
   */
  List<Agent> findByDeadlineAfterAndQtalk(Date deadline, String qtalk);

  /**
   * 根据代理人查询被代理人
   * @param agent
   * @return
   */
  Optional<List<Agent>> findByDeadlineAfterAndAgent(Date deadline, String agent);
}
