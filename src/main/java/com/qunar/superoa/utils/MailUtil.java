package com.qunar.superoa.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.superoa.dao.SuperOAUserRepository;
import com.qunar.superoa.dto.MailInfo;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * @Auther: yang.du
 * @Despriction: 邮件发送工具
 */

@Component
public class MailUtil {

  private Logger logger = LoggerFactory.getLogger(MailUtil.class);

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  @Autowired
  private SuperOAUserRepository superOAUserRepository;

  @Value("${spring.profiles.active}")
  private String environment;

  @Value("${isSendEmail}")
  private String isSendEmail;

  private String username = "qoa@qunar.com";

  /**
   * 线上环境标识
   */
  private static final String PROD = "prod";

  /**
   * 发送邮件
   *
   * @return 是否成功
   */

  public boolean sendMail(MailInfo mailInfo) {
    MimeMessage message = getMimeMessage(mailInfo);
    if (null == message) {
      logger.error("send mail failed");
      return false;
    }
    mailSender.send(message);
    logger.info("send mail success");
    return true;
  }


  /**
   * 封装邮件信息
   */

  public MimeMessage getMimeMessage(MailInfo mailInfo) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      List<String> targetEmails = new ArrayList<>();
      if (null != mailInfo.getTargetEmail()) {
        String[] target = mailInfo.getTargetEmail().split(",");
        for (int i = 0; i < target.length; i++) {
          if (StringUtils.isNotBlank(target[i]) && superOAUserRepository.findByUserName(target[i]).size() == 1) {
            String email = superOAUserRepository.findByUserName(target[i]).get(0).getEmail();
            if (StringUtils.isNotBlank(email)) {
              targetEmails.add(email);
            }

          }
        }
      }
      if (null != mailInfo.getEmailGroup()) {
        targetEmails.add(mailInfo.getEmailGroup());
      }
      String[] to = targetEmails.toArray(new String[targetEmails.size()]);
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      mimeMessageHelper.setFrom(username);
      mimeMessageHelper.setSubject(mailInfo.getPrefix() + mailInfo.getSubject());
      //非线上环境或配置文件不发邮件，邮件发送给管理员
      if (!PROD.equalsIgnoreCase(environment) || "false".equalsIgnoreCase(isSendEmail)) {
        mimeMessageHelper
            .setSubject(targetEmails.toString() + mailInfo.getPrefix() + mailInfo.getSubject());
        targetEmails.removeAll(targetEmails);
        targetEmails.add("test@qunar.com");
        to = targetEmails.toArray(new String[targetEmails.size()]);
      }
      //将内容放入模板
      Map<String, Object> model = Maps.newHashMap();
      model.put("applyUserName", mailInfo.getRelationUserName());
      model.put("applyLink", mailInfo.getLinkUrl());
      model.put("title", mailInfo.getSubject());
      model.put("suffix", mailInfo.getSuffix());
      model.put("QRCodeUrl", mailInfo.getQrCodeUrl());
      String[] contentString = mailInfo.getContent().split(";");
      List<String[]> contentList = Lists.newArrayList();
      for (int i = 0; i < contentString.length; i++) {
        if (StringUtils.isNotBlank(contentString[i])) {
          contentList.add(contentString[i].split(":"));
        }
      }
      model.put("content", contentList);
      try {
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("email.ftl");
        try {
          String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
          mimeMessageHelper.setText(text, true);
        } catch (TemplateException e) {
          logger.error("Template get failed", e);
        }
      } catch (IOException e) {
        logger.error("Mail template created failed", e);
      }
      //收件人
      mimeMessageHelper.setTo(to);
      if (null != mailInfo.getCcListMail()) {
        mimeMessageHelper.setCc(mailInfo.getCcListMail());
      }
    } catch (MessagingException e) {
      logger.error("MessagingException:", e);
      logger.error("create mime message error");
      return null;
    }
    return mimeMessage;
  }
}
