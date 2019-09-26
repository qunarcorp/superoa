package com.qunar.superoa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/23_6:19 PM
 * @Despriction: 提供登陆的逻辑
 */

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // [1] token 中的用户名和密码都是用户输入的，不是数据库里的
    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
    // [2] 使用用户名从数据库读取用户信息
    MySuperOAUserDetails userDetails = userDetailsService.loadUserByUsername(token.getName());

    // [3] 检查用户信息
    if (userDetails == null) {
      throw new UsernameNotFoundException("用户不存在");
    } else if (!userDetails.isEnabled()) {
      throw new DisabledException("用户已被禁用");
    } else if (!userDetails.isAccountNonExpired()) {
      throw new AccountExpiredException("账号已过期");
    } else if (!userDetails.isAccountNonLocked()) {
      throw new LockedException("账号已被锁定");
    } else if (!userDetails.isCredentialsNonExpired()) {
      throw new LockedException("凭证已过期");
    }

    // [4] 根据不同的情况比对密码
    if (isOAuthUser(userDetails)) {
      // 通过 OAuth 登陆过来的，例如密码是调用 SecurityHelper.login() 前判断是 OAuth 合法登陆的，
      // 然后就查询数据库得到本地用户的密码，这里可以只需要和数据库里的使用等于比较就可以了，具体的仍然需要根据业务逻辑调整
      // 如果不涉及到 OAuth 用户登陆，可以删除此段代码
    } else {
      // 数据库用户的密码，一般都是加密过的
      String encryptedPassword = userDetails.getPassword();
      // 用户输入的密码
      String inputPassword = (String) token.getCredentials();
      String salt = userDetails.getSalt();

      // 根据加密算法加密用户输入的密码，然后和数据库中保存的密码进行比较
//      if (!bCryptPasswordEncoder.matches(inputPassword + salt, encryptedPassword)) {
//        throw new BadCredentialsException("用户名/密码无效");
//      }
    }

    // [5] 成功登陆，把用户信息提交给 Spring Security
    // 把 userDetails 作为 principal 的好处是可以放自定义的 MySuperOAUserDetails，这样可以存储更多有用的信息，而不只是 username，
    // 默认只有 username，这里的密码使用数据库中保存的密码，而不是用户输入的明文密码，否则就暴露了密码的明文
    return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
        userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.equals(authentication);
  }

  /**
   * 判断是否通过 OAuth 登陆的用户，例如 QQ，优酷等都提供了 OAuth 第三方登陆服务。
   *
   * @return 如果是 OAuth 用户则返回 true，否则返回 false
   */
  private boolean isOAuthUser(MySuperOAUserDetails userDetails) {
    // 以 QQ_ 开头的用户名，说明是使用 QQ 的 OAuth 登陆的用户
    // 以 YK_ 开头的用户名，说明是使用优酷的 OAuth 登陆的用户
    // ...
    return false;
//        return userDetails.getUsername().startsWith("QQ_");
  }
}
