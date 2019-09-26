package com.qunar.superoa.controller;

import com.qunar.superoa.dto.OAUserDto;
import com.qunar.superoa.dto.PasswordDto;
import com.qunar.superoa.dto.QueryUserDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.OAUserServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 3:11 PM 2019/5/22
 * @Modify by:
 */
@RestController
@Api(value = "oaUserController", tags = "OA用户管理")
@RequestMapping("oaUser")
public class OAUserController {

  @Autowired
  private OAUserServiceI oaUserServiceI;

  @ApiModelProperty("管理员获取全部用户信息")
  @PostMapping("adminGetAllUserInfo")
  public Result adminGetAllUserInfo(@RequestBody QueryUserDto queryUserDto) {
    return ResultUtil.success(oaUserServiceI.adminGetAllUserInfo(queryUserDto));
  }

  @ApiModelProperty("管理员根据id获取某个用户信息")
  @PostMapping("adminGetUserInfo")
  public Result adminGetUserInfo(@RequestBody OAUserDto oaUserDto) {
    return ResultUtil.success(oaUserServiceI.adminGetUserInfo(oaUserDto.getId()));
  }

  @ApiModelProperty("根据关键字获取用户信息")
  @PostMapping("search")
  public Result search(@RequestBody QueryUserDto queryUserDto) {
    return ResultUtil.success(oaUserServiceI.searchOAUser(queryUserDto.getKey()));
  }

  @ApiModelProperty("用户获取自身信息")
  @GetMapping("getUserInfo")
  public Result getUserInfo() {
    return ResultUtil.success(oaUserServiceI.getUserInfo());
  }

  @ApiModelProperty("管理员添加用户")
  @PostMapping("addOAUser")
  public Result adminAddOAUser(@RequestBody OAUserDto oaUserDto) {
    return ResultUtil.success(oaUserServiceI.adminAddOAUser(oaUserDto));
  }

  @ApiModelProperty("管理员更新用户信息")
  @PostMapping("adminUpdateOAUser")
  public Result adminUpdateOAUser(@RequestBody OAUserDto oaUserDto) {
    return ResultUtil.success(oaUserServiceI.adminUpdateOAUser(oaUserDto));
  }

  @ApiModelProperty("管理员更新用户密码")
  @PostMapping("adminUpdatePassword")
  public Result adminUpdatePassword(@RequestBody PasswordDto passwordDto) {
    return ResultUtil.success(oaUserServiceI.adminUpdateOAUserPassword(passwordDto));
  }

  @ApiModelProperty("管理员根据id删除用户")
  @PostMapping("delOAUserById")
  public Result delOAUserById(@RequestBody OAUserDto oaUserDto) {
    return ResultUtil.success(oaUserServiceI.adminDeleteOAUserById(oaUserDto.getId()));
  }

  @ApiModelProperty("用户更新自身信息")
  @PostMapping("updateOAUserSelf")
  public Result updateOAUserSelf(@RequestBody OAUserDto oaUserDto) {
    return ResultUtil.success(oaUserServiceI.updateOAUserSelf(oaUserDto));
  }

  @ApiModelProperty("用户修改密码")
  @PostMapping("updatePassword")
  public Result updatePassword(@RequestBody PasswordDto passwordDto) {
    return ResultUtil.success(oaUserServiceI.updatePassword(passwordDto));
  }

  @ApiModelProperty("用户更新头像")
  @PostMapping("updateAvatar")
  public Result updateAvatar(@RequestParam("file") MultipartFile file) throws Exception {
    return ResultUtil.success(oaUserServiceI.updateAvatar(file));
  }

}
