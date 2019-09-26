package com.qunar.superoa.security;

import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.model.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_6:05 PM
 * @Despriction: 自定义的 MySuperOAUserDetails 可以保存用户的其他信息到 session 里, 例如 user id 等.
 */

public class MySuperOAUserDetails extends SuperOAUser implements UserDetails {

  private List<UserRole> roles;

  public MySuperOAUserDetails(SuperOAUser superOAUser, List<UserRole> roles) {
    super(superOAUser);
    this.roles = roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (roles == null || roles.size() < 1) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList("");
    }
    StringBuilder commaBuilder = new StringBuilder();
    for (UserRole role : roles) {
      commaBuilder.append(role.getRoleType()).append(",");
    }
    String authorities = commaBuilder.substring(0, commaBuilder.length() - 1);
    return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
  }

  @Override
  public String getPassword() {
    return super.getPassword();
  }

  @Override
  public String getUsername() {
    return super.getUserName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
