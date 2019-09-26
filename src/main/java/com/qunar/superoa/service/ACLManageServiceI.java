package com.qunar.superoa.service;

import com.qunar.superoa.model.ACLManage;

import java.util.Optional;

/**
 * @author zhouxing
 * @date 2019-03-10 15:30
 */
public interface ACLManageServiceI {
  /**
   * 根据IP查询aclmanage
   * @param ip
   * @return
   */
  Optional<ACLManage> getAclManageByIp(String ip);
}
