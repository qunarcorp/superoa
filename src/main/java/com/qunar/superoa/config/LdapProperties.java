package com.qunar.superoa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: lee.guo
 * @Despriction: 获取配置文件中的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ldap")
public class LdapProperties {
    String ldap_host;
    String ldap_user;
    String ldap_pwd;
    String ldap_basedn;
    String ldap_basemail;
    String ldap_cn;
}
