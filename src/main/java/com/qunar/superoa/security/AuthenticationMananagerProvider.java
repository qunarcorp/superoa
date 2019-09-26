package com.qunar.superoa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configurable
@EnableWebSecurity
/**允许进入页面方法前检验*/
@EnableGlobalMethodSecurity(prePostEnabled = true)
/**
 * @author chengyan.liang
 * @author: lee.guo
 * @Date:Created in 2018/9/26_上午11:43
 * @Despriction: 登录权限控制
 */
public class AuthenticationMananagerProvider extends WebSecurityConfigurerAdapter {

  @Autowired
  /**自定义验证*/
  private MyAuthenticationProvider provider;

  @Autowired
  /**自定义用户服务*/
  private UserDetailsService userDetailsService;

  @Autowired
  private SessionRegistry sessionRegistry;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //将验证过程交给自定义验证工具
    auth.authenticationProvider(provider);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http.csrf().disable();
//    // 省略其他代码；
//  }

//    /**
//     * HTTP请求处理
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .formLogin().loginPage("/#/user/login") //自定义URL
//            .defaultSuccessUrl("/free/list.do")//启用FORM登录
//                .and().authorizeRequests().antMatchers("/user/login.do").permitAll()//登录页允许所有人访问
//                .and().authorizeRequests().antMatchers("/**/*.do").authenticated()
//                .and().httpBasic()
//                .and().csrf().disable();  //暂时禁用CSRF
//        ;
//    }

  /**
   * 授权验证服务
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .and().authorizeRequests()
        .antMatchers("/**/**").permitAll()
//        .antMatchers("/admin/**").hasRole("ADMIN")
//        .antMatchers("/**").hasRole("USER")
//        .and().formLogin().loginPage("/login.jsp").permitAll().loginProcessingUrl("/login")
        .and().logout().permitAll()
        //自动识别tokenRepository类型，启用PersistentTokenBasedRememberMeServices
        .and().rememberMe()
        //Spring Security的默认启用防止固化session攻击
        .and().sessionManagement().sessionFixation().migrateSession()
        //设置session最大并发数为1，当建立新session时，原session将expired，并且跳转到登录界面
        .maximumSessions(1).expiredUrl("/doc.html")
        .sessionRegistry(sessionRegistry)
        .and()
        .and().csrf().disable();
  }

  //    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        /*auth.inMemoryAuthentication()
//        .withUser("simm").password("{noop}123").roles("USER").and()
//        .withUser("admin").password("{noop}admin").roles("USER","ADMIN");*/
//
//        auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
//                .withUser("simm").password("123").roles("USER").and()
//                .withUser("admin").password("admin").roles("USER","ADMIN");
//    }
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

}