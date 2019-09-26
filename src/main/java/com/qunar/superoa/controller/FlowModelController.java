package com.qunar.superoa.controller;

import com.qunar.superoa.dto.DeptDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.FlowModelNameDto;
import com.qunar.superoa.dto.FlowStatusDto;
import com.qunar.superoa.dto.FormKeyDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.service.FlowModelServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: xing.zhou
 * @Auther: chengyan.liang
 * @Date:Created in 2018/9/28_3:23 PM
 * @Despriction:
 */

@Slf4j
@Api(value = "FlowModelController", tags =  "流程模板相关")
@RestController
@RequestMapping("flowModel")
public class FlowModelController {

  @Autowired
  private FlowModelServiceI flowModelServiceI;


  @ApiOperation("添加流程模板")
  @PostMapping(value = "addFlowMode")
  public Result<FlowModelDto> addFlowMode(@RequestBody FlowModelDto flowModelDto) throws Exception {
    return ResultUtil.success(flowModelServiceI.addFlowMode(flowModelDto));
  }

  @ApiOperation("修改流程模板")
  @PostMapping(value = "updateFlowMode")
  public Result<FlowModelDto> updateFlowMode(@RequestBody FlowModelDto flowModelDto)
      throws Exception {
    return ResultUtil.success(flowModelServiceI.updateFlowMode(flowModelDto));
  }


  @ApiOperation("查询有模板的全部部门")
  @GetMapping(value = "getFlowModelAllDept")
  public Result<List<String>> getFlowModelAllDept() {
    return ResultUtil.success(flowModelServiceI.getFlowModelAllDept());
  }

  @ApiOperation("查询全部部门")
  @GetMapping(value = "getAllDept")
  public Result<List<String>> getAllDept() {
    return ResultUtil.success(flowModelServiceI.getAllDeptByCompany());
  }


  @ApiOperation("根据条件获取流程模板")
  @PostMapping(value = "getFlowModelsByDept")
  public Result<List<FlowModelNameDto>> getFlowModelsByDept(@RequestBody DeptDto dept)
      throws Exception {
    return ResultUtil.success(flowModelServiceI.getFlowModels(dept));
  }


  @ApiOperation("分页获取流程模板")
  @PostMapping(value = "getFlowModelsPageAble")
  public Result<List<FlowModel>> getFlowModelsPageAble(@RequestBody DeptDto dept) {
    return ResultUtil.success(flowModelServiceI.getFlowModelsPageAble(dept));
  }


  @ApiOperation("查询热门流程模板")
  @PostMapping(value = "getMyFlowModels")
  public Result<List<FlowModelNameDto>> getMyFlowModels() {
    return ResultUtil.success(flowModelServiceI.getMyFlowModels());
  }


  @ApiOperation("根据流程模板key查询流程模板")
  @PostMapping(value = "getFlowModelByFormKey")
  public Result<FlowModelDto> getFlowModelByFormKey(@RequestBody FormKeyDto formKeyDto) {
    return ResultUtil.success(flowModelServiceI.getFlowModelDtoByFormKeyAndVersion(formKeyDto.getFormKey(), formKeyDto.getFormVersion()));
  }

  @ApiOperation("更新表单状态")
  @PostMapping(value = "updateFlowModelStatus")
  public Result<FlowModelDto> updateFlowModeStatus(@RequestBody FlowStatusDto flowStatusDto) {
    return ResultUtil.success(flowModelServiceI.updateFlowModelStatus(flowStatusDto));
  }

}
