package com.qunar.superoa.controller;

import com.qunar.superoa.dto.ExtSysDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.ExtSysFlowModelServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: chengyan.liang
 * @Despriction: 外部系统集成发起流程
 * @Date:Created in 7:40 PM 2019/4/29
 * @Modify by:
 */
@Slf4j
@Api(value = "extSysFlowModelController", tags =  "外部系统集成发起流程")
@RestController
@RequestMapping("extSysFlowModel")
public class ExtSysFlowModelController {

  @Autowired
  private ExtSysFlowModelServiceI extSysFlowModelServiceI;

  @ApiOperation(value = "添加/更新发起流程")
  @PostMapping("update")
  public Result update(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysFlowModelServiceI.updateExtSysFlowModel(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "删除发起流程")
  @PostMapping("remove")
  public Result remove(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysFlowModelServiceI.deleteExtSysFlowModel(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "批量添加/删除发起流程")
  @PostMapping("update_patch")
  public Result update_patch(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysFlowModelServiceI.updatePatchExtSysFlowModel(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "获取当前登录人的外部系统可发起流程")
  @PostMapping("getExtSysFlowModels")
  public Result getExtSysFlowModels() {
    return ResultUtil.success(extSysFlowModelServiceI.getAllExtSysFlowModel());
  }

  @ApiOperation(value = "获取当前登录人的所有可发起流程的外部系统标识")
  @PostMapping("getExtSysFlowModelProcessKeys")
  public Result getExtSysFlowModelProcessKeys() {
    return ResultUtil.success(extSysFlowModelServiceI.getExtSysFlowModelProcessKeys());
  }
}
