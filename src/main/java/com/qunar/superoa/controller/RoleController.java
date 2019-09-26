package com.qunar.superoa.controller;

import com.qunar.superoa.dto.AdminDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ipml.UserRoleService;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 14:45 2018/8/31
 */
@Api(value = "RoleController", tags = "角色相关接口")
@RestController
@RequestMapping("role")
public class RoleController {

  @Autowired
  private UserRoleService userRoleService;

  /**
   * 获取当前登录用户所拥有的角色
   */
  @ApiOperation("获取当前登录用户角色")
  @GetMapping("getUserRole")
  public Result getUserRole() {
    return ResultUtil.success(userRoleService.getRolesWithCurrentUser());
  }

  @ApiOperation("根据用户名获取用户角色")
  @GetMapping("getUserRoleByUserName")
  public Result getUserRoleByUserName(@RequestParam String userName) throws Exception {
    return ResultUtil.run(() -> userRoleService.getRoleByUserName(userName));
  }

  @ApiOperation("获取所有管理员用户角色")
  @GetMapping("getAllUserRole")
  public Result getAllUserRole() throws Exception {
    return ResultUtil.run(() -> userRoleService.getAllRoleUser());
  }

  /**
   * 根据前端传来的用户名和角色名称为用户添加角色
   */
  @ApiOperation("给用户添加角色")
  @PostMapping("addRoles")
  @ResponseBody
  public Result addRoles(@RequestBody AdminDto adminDto) {
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    if (userRoleService.addRole(adminDto.getUserName(), adminDto.getRoles())) {
      return ResultUtil.success();
    } else {
      return ResultUtil.error(-100, "参数错误或者该用户已拥有此角色");
    }
  }

  @ApiOperation("删除用户的角色")
  @PostMapping("removeRole")
  public Result removeRole(@RequestParam String userName, @RequestParam String roleType) {
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    if (userRoleService.removeRoleByUserNameAndRoleType(userName, roleType)) {
      return ResultUtil.success();
    } else {
      return ResultUtil.error(-100, "参数错误");
    }
  }


  @ApiOperation("删除该用户所有角色")
  @PostMapping("removeAllRoles")
  public Result removeAllRoles(@RequestBody AdminDto adminDto) {
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    if (userRoleService.removeRolesByUserName(adminDto.getUserName())) {
      return ResultUtil.success();
    } else {
      return ResultUtil.error(-100, "参数错误");
    }
  }

}
