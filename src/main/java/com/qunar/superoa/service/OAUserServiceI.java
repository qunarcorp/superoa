package com.qunar.superoa.service;

import com.qunar.superoa.dto.OAUserDto;
import com.qunar.superoa.dto.PasswordDto;
import com.qunar.superoa.dto.QueryUserDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:33 AM 2019/5/22
 * @Modify by:
 */
public interface OAUserServiceI {

  /**
   * 管理员获取所有用户信息
   */
  Object adminGetAllUserInfo(QueryUserDto queryUserDto);

  /**
   * 管理员根据id获取某个用户信息
   */
  Object adminGetUserInfo(String id);

  /**
   * 用户获取自身信息
   */
  Object getUserInfo();

  /**
   * 管理员添加用户
   */
  Object adminAddOAUser(OAUserDto oaUserDto);

  /**
   * 管理员更新用户信息
   */
  Object adminUpdateOAUser(OAUserDto oaUserDto);

  /**
   * 管理员变更用户密码
   */
  Object adminUpdateOAUserPassword(PasswordDto passwordDto);

  /**
   * 管理员根据id删除用户
   */
  Object adminDeleteOAUserById(String id);

  /**
   * 用户更新自身信息
   */
  Object updateOAUserSelf(OAUserDto oaUserDto);

  /**
   * 根据关键字查询用户列表
   */
  Object searchOAUser(String key);

  /**
   * 用户更新密码
   */
  Object updatePassword(PasswordDto passwordDto);

  /**
   * 用户更新头像
   */
  Object updateAvatar(MultipartFile imageFile) throws Exception;

}
