package com.qunar.superoa.controller;

import com.qunar.superoa.dto.ExtSysDto;
import com.qunar.superoa.dto.QueryExtSysUnapproveDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.ExtSysUnapproveFlowServiceI;
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
 * @Despriction:
 * @Date:Created in 11:59 AM 2019/4/28
 * @Modify by:
 */
@Slf4j
@Api(value = "extSysUnapproveFlowController", tags =  "外部系统集成待审批流程")
@RestController
@RequestMapping("extSysUnapproveFlow")
public class ExtSysUnapproveFlowController {

  @Autowired
  private ExtSysUnapproveFlowServiceI extSysUnapproveFlowServiceI;

  @ApiOperation(value = "添加/更新待审批流程")
  @PostMapping("update")
  public Result update(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysUnapproveFlowServiceI.updateExtSysUnapproveFlow(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "删除待审批流程")
  @PostMapping("remove")
  public Result remove(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysUnapproveFlowServiceI.deleteExtSysUnapproveFlow(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "批量添加/删除待审批流程")
  @PostMapping("update_patch")
  public Result update_patch(@RequestBody ExtSysDto extSysDto) {
    return ResultUtil.success(extSysUnapproveFlowServiceI.updatePatchExtSysUnapproveFlow(extSysDto.getDataSource()));
  }

  @ApiOperation(value = "获取当前登录人的外部系统待审批/发起流程")
  @PostMapping("getUnapproveFlows")
  public Result getUnapproveFlows(@RequestBody QueryExtSysUnapproveDto queryUnapproveDto) {
    return ResultUtil.success(extSysUnapproveFlowServiceI.getMyExtSysUnapproveFlows(queryUnapproveDto));
  }

  @ApiOperation(value = "获取当前用户待审批流程所涉及到的所有外部系统标识")
  @PostMapping("getProcessKeys")
  public Result getProcessKeys() {
    return ResultUtil.success(extSysUnapproveFlowServiceI.getMyExtSysUnapproveProcessKeys());
  }

  @ApiOperation(value = "获取外部系统我的待办流程数(当前登录人)")
  @PostMapping("getToApproveCount")
  public Result getToApproveCount() {
    return ResultUtil.success(extSysUnapproveFlowServiceI.getExtSysToApproveCount());
  }

  @ApiOperation(value = "获取外部系统当前用户发起的审批中流程数")
  @PostMapping("getMyFlowCount")
  public Result getMyFlowCount() {
    return ResultUtil.success(extSysUnapproveFlowServiceI.getMyExtSysFlowCount());
  }

}
