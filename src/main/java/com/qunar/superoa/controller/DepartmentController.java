package com.qunar.superoa.controller;

import com.qunar.superoa.dto.DepartmentDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.DepartmentServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:22 PM 2019/5/21
 * @Modify by:
 */
@Api(value = "departmentController", tags = "部门增删改查")
@RestController
@RequestMapping("department")
public class DepartmentController {

  @Autowired
  private DepartmentServiceI departmentServiceI;

  @ApiModelProperty(value = "获取整个部门树")
  @GetMapping(value = "getDeptTrees")
  public Result getDeptTrees() {
    return ResultUtil.success(departmentServiceI.getDeptTrees());
  }

  @ApiModelProperty(value = "获取部门详细信息")
  @PostMapping(value = "getDeptInfo")
  public Result getDeptInfo(@RequestBody DepartmentDto departmentDto) {
    return ResultUtil.success(departmentServiceI.getDeptInfo(departmentDto.getId()));
  }

  @ApiModelProperty(value = "添加新部门")
  @PostMapping(value = "addDepartment")
  public Result addDepartment(@RequestBody DepartmentDto departmentDto) {
    return ResultUtil.success(departmentServiceI.addDepartment(departmentDto));
  }

  @ApiModelProperty(value = "更新部门信息")
  @PostMapping(value = "updateDepartment")
  public Result updateDepartment(@RequestBody DepartmentDto departmentDto) {
    return ResultUtil.success(departmentServiceI.updateDepartment(departmentDto));
  }

  @ApiModelProperty(value = "删除部门")
  @PostMapping(value = "deleteDepartment")
  public Result deleteDepartment(@RequestBody DepartmentDto departmentDto) {
    return ResultUtil.success(departmentServiceI.deleteDepartment(departmentDto.getId()));
  }

}
