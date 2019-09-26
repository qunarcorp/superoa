package com.qunar.superoa.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: lee.guo
 * @Despriction:
 * @Date: Created in 6:12 PM 2018/10/26
 * @Modify by:
 */
@Configuration
@ComponentScan(basePackages = "com.qunar.superoa.task")
public class XxlJobConfig {
  private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

  @Value("${xxl.job.admin.addresses}")
  private String adminAddresses;

  @Value("${xxl.job.executor.appname}")
  private String appName;

  @Value("${xxl.job.executor.ip}")
  private String ip;

  @Value("${xxl.job.executor.port}")
  private int port;

  @Value("${xxl.job.accessToken}")
  private String accessToken;

  @Value("${xxl.job.executor.logpath}")
  private String logPath;

  @Value("${xxl.job.executor.logretentiondays}")
  private int logRetentionDays;

  @Value("${spring.profiles.active}")
  private String environment;

  @Bean(initMethod = "start", destroyMethod = "destroy")
  public XxlJobExecutor xxlJobExecutor() {
    if(!"dev".equals(environment)){
      logger.info(">>>>>>>>>>> xxl-job 配置初始化");
      XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
      xxlJobExecutor.setAdminAddresses(adminAddresses);
      xxlJobExecutor.setAppName(appName);
      xxlJobExecutor.setIp(ip);
      xxlJobExecutor.setPort(port);
      xxlJobExecutor.setAccessToken(accessToken);
      xxlJobExecutor.setLogPath(logPath);
      xxlJobExecutor.setLogRetentionDays(logRetentionDays);
      return xxlJobExecutor;
    }
    return null;
  }

}
