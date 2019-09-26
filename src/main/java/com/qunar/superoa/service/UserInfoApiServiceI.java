package com.qunar.superoa.service;

import com.qunar.superoa.dto.StarTalkUserDetailBody;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Auther: chengyan.liang
 * @Despriction: 获取员工信息接口(姓名、部门、直属领导、部门总监、hrbp、hrd等)
 * @Date:Created in 2:34 PM 2019/5/10
 * @Modify by:
 */
@Service
@FeignClient(url = "${api.starTalk}", name = "userDetailApi")
public interface UserInfoApiServiceI {

  /**
   * 获取用户汇报线关系、部门等信息
   */
  @PostMapping(value = "/newapi/getOa.qunar", produces = "application/json;charset=utf-8")
  String getUserInfoByUserId(@RequestBody Map<String, String> userInfoBody);

  /**
   * 获取用户详细信息包括姓名、性别、签名、头像地址等信息
   */
  @PostMapping(value = "/corp/userinfo/get_user_info.qunar", produces = "application/json;charset=utf-8")
  String getUserDetailByUserId(@RequestBody StarTalkUserDetailBody starTalkUserDetailBody);

}
