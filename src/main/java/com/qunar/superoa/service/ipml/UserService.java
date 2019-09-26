package com.qunar.superoa.service.ipml;

import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.OAUserRepository;
import com.qunar.superoa.dto.StarTalkUserDetailBody;
import com.qunar.superoa.dto.StarTalkUserDto;
import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.service.UserInfoApiServiceI;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.DateTimeUtil;
import com.qunar.superoa.utils.UserInfoUtil;
import com.qunar.superoa.utils.UserInfoUtilFromStarTalk;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserService {

  @Autowired
  private UserInfoApiServiceI userInfoApiServiceI;

  @Autowired
  private UserInfoUtil userInfoUtil;

  @Autowired
  private UserInfoUtilFromStarTalk userInfoUtilFromStarTalk;

  @Autowired
  private OAUserRepository userRepository;

  /**
   * 根据qtalkID 查找用户信息 并 保存
   */
  public SuperOAUser findByName(String userId) {
    SuperOAUser superOAUser = new SuperOAUser();

    superOAUser.setAvatar(Constant.OA_USER_AVATAR);

    try {

      //starTalk接口获取用户基本信息
      Map<String, Object> userDetailMap = CommonUtil
          .o2m(userInfoApiServiceI.getUserDetailByUserId(new StarTalkUserDetailBody(userId)));

      try {
        //starTalk接口请求失败 考虑从数据库和用户汇报线userInfo接口拿数据
        if (!"0.0".equals(userDetailMap.get("errcode") + "")) {
          return getUserDetailFromDB(userId);
        }
        return userRepository
            .save(new SuperOAUser(new StarTalkUserDto(CommonUtil.o2A(userDetailMap.get("data")))));
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    } catch (Exception e) {
      log.error("查询{}的账号信息失败， 可能是qtalk接口挂掉了！", userId);
      log.error(e.getMessage());
      return getUserDetailFromDB(userId);
    }
    return null;
  }

  /**
   * 根据qtalkID 查找用户头像URL并返回
   */
  public String findUserAvatarByName(String userName) {
    List<SuperOAUser> superOAUserList = userRepository.findByUserName(userName);
    //判断数据库中是否存在此用户信息，不存在则添加，存在则返回
    SuperOAUser superOAUser =
        superOAUserList.isEmpty() ? findByName(userName) : superOAUserList.get(0);
    return superOAUser == null || StringUtils.isBlank(superOAUser.getAvatar()) ? Constant.OA_USER_AVATAR
        : superOAUser.getAvatar();
  }

  /**
   * 根据qtalkID 查找用户姓名并返回
   */
  public String findUserCNameByName(String userName) {
    List<SuperOAUser> superOAUserList = userRepository.findByUserName(userName);
    //判断数据库中是否存在此用户信息，不存在则添加，存在则返回
    return superOAUserList.isEmpty() ? (findByName(userName) == null ? userName : findByName(userName).getCname())
        : superOAUserList.get(0).getCname();
  }


  /**
   * 从数据库获取用户详细信息
   */
  private SuperOAUser getUserDetailFromDB(String userId) {
    // 数据库中获取用户信息
    List<SuperOAUser> dbUserInfo = userRepository.findByUserName(userId);

    // 数据库无此数据 从用户汇报线关系接口中拿数据
    if (dbUserInfo.size() == 0) {
      try {
        Map<String, Object> userInfoMap = userInfoUtilFromStarTalk.getUserInfo(userId);
        SuperOAUser oaUser = new SuperOAUser();
        oaUser.setAvatar(Constant.OA_USER_AVATAR);
        oaUser.setCname(userInfoMap.get("userName") + "");
        oaUser.setGender("1");
        oaUser.setMood("今天心情美美哒~");
        oaUser.setUpdateTime(DateTimeUtil.getDateTime());
        oaUser.setUserName(userId);
        return userRepository.save(oaUser);
      } catch (Exception e) {
        log.error("查询{}账号信息失败， 可能是userInfo汇报线接口挂了", userId);
        log.error(e.getMessage());
        return null;
      }
    }
    return dbUserInfo.get(0);
  }
}
