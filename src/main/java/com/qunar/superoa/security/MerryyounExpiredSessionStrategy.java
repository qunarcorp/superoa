package com.qunar.superoa.security;

import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.SuperOALoginException;
import java.io.IOException;
import javax.servlet.ServletException;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/10/8_11:35 AM
 * @Despriction:
 */


public class MerryyounExpiredSessionStrategy implements SessionInformationExpiredStrategy {

  @Override
  public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
      throws IOException, ServletException {
    throw new SuperOALoginException(ResultEnum.LOGIN_OTHER);
  }
}
