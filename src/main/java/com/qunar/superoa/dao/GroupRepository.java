package com.qunar.superoa.dao;

import com.qunar.superoa.model.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/17_上午10:24
 * @Despriction:
 */

public interface GroupRepository extends JpaRepository<WorkGroup, Integer>,JpaSpecificationExecutor<WorkGroup> {

    /**
     * 根据年龄查询
     * 方法名必须按照固定格式书写
     * @param name
     * @return girl list
     */
    List<WorkGroup> findByName(Integer name);

    Optional<WorkGroup> findByName(String name);
}
