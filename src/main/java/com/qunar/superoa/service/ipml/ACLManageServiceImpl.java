package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.ACLManageRepository;
import com.qunar.superoa.model.ACLManage;
import com.qunar.superoa.service.ACLManageServiceI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author zhouxing
 * @date 2019-03-10 15:30
 */
@Service
@Slf4j
public class ACLManageServiceImpl implements ACLManageServiceI {

  @Autowired
  private ACLManageRepository aclManageRepository;

  @Override
  public Optional<ACLManage> getAclManageByIp(String ip) {
    return aclManageRepository.getACLManageByIpsLike("%" + ip + "%");
  }
}
