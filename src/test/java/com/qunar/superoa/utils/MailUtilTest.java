package com.qunar.superoa.utils;

import com.qunar.superoa.dto.MailInfo;
import com.qunar.superoa.model.Notify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MailUtilTest {

    private Logger logger = LoggerFactory.getLogger(MailUtilTest.class);

    @Autowired
    private MailUtil mailUtil;
    @Value("${spring.mail.username}")
    private String username;


    @Test
    public void getMimeMessage() {

    }
}