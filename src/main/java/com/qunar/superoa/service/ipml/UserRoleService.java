package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.UserRoleRepository;
import com.qunar.superoa.dto.RoleDto;
import com.qunar.superoa.dto.UserRoleDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.enums.RoleTypeEnum;
import com.qunar.superoa.exceptions.RoleException;
import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.model.UserRole;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.UserRoleServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserRoleService implements UserRoleServiceI {

  private Logger logger = LoggerFactory.getLogger(UserRoleService.class);

  @Autowired
  private UserRoleRepository userRoleRepository;


  /**
   * 获取当前登录用户的角色
   */
  @Override
  public List<RoleDto> getRolesWithCurrentUser() {
    String userName = SecurityUtils.currentUsername();
    if (null == userName || userName.equals("anonymousUser")) {
      logger.info("| 登录用户为空");
      throw new RoleException(ResultEnum.NO_USER);
    }
    List<RoleDto> roleDtos = getRoleByUserName(userName);
    return roleDtos;
  }

  /**
   * 根据User对象获取角色
   *
   * @return 用户角色关系
   */
  @Override
  public List<UserRole> getRoleByUser(SuperOAUser superOAUser) {
    String userName = superOAUser.getUserName();
    if (null == userName) {
      logger.info("| 用户名称为空");
      return null;
    }
    List<UserRole> userRoles = userRoleRepository.findByQtalk(userName);
    return userRoles;
  }


  /**
   * 根据username获取角色
   */
  @Override
  public List<RoleDto> getRoleByUserName(String userName) {
    if (null == userName || "".equals(userName)) {
      logger.info("| 用户名称为空");
      throw new RoleException(ResultEnum.EMPTY_USERNAME);
    }
    List<RoleDto> roleDtos = new ArrayList<>();
    List<UserRole> userRoles = userRoleRepository.findByQtalk(userName);
    if (userRoles.size() == 0) {
      roleDtos.add(new RoleDto("ROLE_USER"));
    } else {
      userRole2RoleDto(userRoles, roleDtos);
    }
    return roleDtos;
  }

  @Override
  public List<UserRoleDto> getAllRoleUser() {
    List<Map<String, String>> maps = userRoleRepository.findAllUser();
    List<UserRoleDto> userRoleDtos = new ArrayList<>();
    maps.forEach(map -> userRoleDtos.add(new UserRoleDto(map.get("username"), map.get("role"), map.get("roleName"))));
    return userRoleDtos;
  }

  /**
   * 根据userName和roleType获取角色对象
   */
  @Override
  public List<UserRole> getRoleByUserNameAndRoleType(String userName, String roleType) {
    if (null == userName || null == roleType) {
      logger.info("| userName和roleType不能为空");
      return null;
    }
    List<UserRole> userRoles = userRoleRepository.findByQtalkAndRoleType(userName, roleType);
    return userRoles;
  }

  @Transactional
  @Override
  public boolean addRole(String userName, List<String> roles) {
    if (null == userName || "".equals(userName.trim())) {
      logger.info("| 用户名不能为空");
      throw new RoleException(ResultEnum.EMPTY_USERNAME);
    }
    for (int i = 0; i < roles.size(); i++) {
      RoleTypeEnum roleTypeEnum = RoleTypeEnum.getEnumByText(roles.get(i));
      if (null == roleTypeEnum) {
        logger.info("| 角色名称输入错误");
        throw new RoleException(ResultEnum.ROLETYPE_ERROR);
      }
      List<UserRole> userRoles = getRoleByUserNameAndRoleType(userName, roleTypeEnum.getText());
      if (!userRoles.isEmpty() || userRoles.size() > 0) {
        logger.info("| 该用户已拥有该角色");
        throw new RoleException(
            ResultEnum.REPETITION_ROLETYPE.getCode(),
            ResultEnum.REPETITION_ROLETYPE.getMsg()
                .replaceAll("userName", userName)
                .replaceAll("roleType", new StringBuilder()
                    .append(roleTypeEnum.getText()).append("(")
                    .append(roleTypeEnum.getDesc()).append(")").toString()));
      }
      UserRole userRole = new UserRole();
      userRole.setQtalk(userName);
      userRole.setRoleType(roleTypeEnum.getText());
      userRole.setRoleName(roleTypeEnum.getDesc());
      userRoleRepository.save(userRole);
      logger.info(new StringBuilder().append("| 已为").append(userName).append("添加")
          .append(roleTypeEnum.getDesc()).append("角色。").toString());
    }
    return true;
  }

  @Transactional
  @Override
  public boolean modifyRole(UserRole userRole) {
    if (null == userRole) {
      return false;
    }
    userRoleRepository.saveAndFlush(userRole);
    return true;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public boolean removeRole(UserRole userRole) {
    if (null == userRole.getId()) {
      return false;
    }
    userRoleRepository.deleteById(userRole.getId());
    return true;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public boolean removeRoleByUserNameAndRoleType(String userName, String roleType) {
    if (null == userName || "".equals(userName.trim())) {
      throw new RoleException(ResultEnum.EMPTY_USERNAME);
    }
    RoleTypeEnum roleTypeEnum = RoleTypeEnum.getEnumByText(roleType);
    if (null == roleTypeEnum) {
      throw new RoleException(ResultEnum.ROLETYPE_ERROR);
    }
    List<UserRole> userRoles = userRoleRepository.findByQtalkAndRoleType(userName, roleType);
    if (userRoles.size() == 0) {
      throw new RoleException(ResultEnum.NO_OWN_ROLETYPE);
    }
    int flag = userRoleRepository.deleteByQtalkAndRoleType(userName, roleType);
    if (flag > 0) {
      return true;
    } else {
      return false;
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public boolean removeRolesByUserName(String userName) {
    if (null == userName || "".equals(userName.trim())) {
      throw new RoleException(ResultEnum.EMPTY_USERNAME);
    }
    List<UserRole> userRoles = userRoleRepository.findByQtalk(userName);
    if (userRoles.size() == 0) {
      throw new RoleException(ResultEnum.NO_ADMIN);
    }
    int flag = userRoleRepository.deleteByQtalk(userName);
    if (flag > 0) {
      return true;
    } else {
      return false;
    }
  }

  public List<RoleDto> userRole2RoleDto(List<UserRole> userRoles, List<RoleDto> roleDtos) {
    userRoles.forEach(userRole -> roleDtos.add(new RoleDto(userRole.getRoleType())));
    return roleDtos;
  }

//    @Override
//    public List<UserRole> getRoleByUser(SuperOAUser user) {
//        List<UserRole> list = new ArrayList();
//        UserRole ur = new UserRole();
//        if("lee.guo".equals(user.getUserName())){
//            ur.setRoleType("ROLE_ACTIVITI_ADMIN");
//        }else{
//            ur.setRoleType("ROLE_ACTIVITI_USER");
//        }
//        list.add(ur);
//         return list;
//    }
}
