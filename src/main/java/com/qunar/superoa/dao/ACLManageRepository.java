package com.qunar.superoa.dao;

import com.qunar.superoa.model.ACLManage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author zhouxing
 * @date 2019-03-10 15:36
 */

public interface ACLManageRepository extends JpaRepository<ACLManage, String> {

  Optional<ACLManage> getACLManageByIpsLike(String ip);
}
