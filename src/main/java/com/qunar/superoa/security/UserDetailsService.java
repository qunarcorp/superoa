package com.qunar.superoa.security;


import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.model.UserRole;
import com.qunar.superoa.service.ipml.UserRoleService;
import com.qunar.superoa.service.ipml.UserService;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_6:12 PM
 * @Despriction: 使用用户名从数据库查找到用户的信息，然后构建 MySuperOAUserDetails
 */

@Slf4j
@Service
public class UserDetailsService implements
    org.springframework.security.core.userdetails.UserDetailsService {

  @Resource
  private UserService userService;

  @Resource
  private UserRoleService userRoleService;

  /**
   * 使用 username 加载用户的信息，如密码，权限等
   *
   * @param userName 登陆表单中用户输入的用户名
   */

  @Override
  public MySuperOAUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    SuperOAUser superOAUser;
    try {
      superOAUser = userService.findByName(userName);
    } catch (Exception e) {
      throw new UsernameNotFoundException(userName + " : superOAUser select fail");
    }
    if (superOAUser == null) {
      throw new UsernameNotFoundException("no superOAUser found");
    } else {
      try {
        List<UserRole> roles = userRoleService.getRoleByUser(superOAUser);
        return new MySuperOAUserDetails(superOAUser, roles);
      } catch (Exception e) {
        throw new UsernameNotFoundException("superOAUser role select fail");
      }
    }
  }
}
