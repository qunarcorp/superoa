package com.qunar.superoa.controller;

import com.google.gson.Gson;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dto.ApproveCountDto;
import com.qunar.superoa.dto.DoFlowDto;
import com.qunar.superoa.dto.FlowCountDto;
import com.qunar.superoa.dto.FlowDataDto;
import com.qunar.superoa.dto.FlowOrderDto;
import com.qunar.superoa.dto.FlowUserDto;
import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.dto.QueryFlowDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.FlowOrderServiceI;
import com.qunar.superoa.service.FlowServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xing.zhou on 2018/9/5.
 * Modified by chengyan.liang on 2018/10/9.
 * Modified by lee.guo on 2018/10/25
 */
@Slf4j
@Api(value = "流程审批相关", tags =  "流程审批相关")
@RestController
@RequestMapping("flow")
public class FlowController {

  @Autowired
  private FlowServiceI flowServiceI;

  @Autowired
  private FlowOrderServiceI flowOrderServiceI;


  @ApiOperation("获取当前用户草稿、审批中、已完结、OA待审批流程数")
  @GetMapping(value = "getFlowCount")
  public Result<FlowCountDto> getFlowCount() {
    return ResultUtil.success(flowOrderServiceI.getFlowCount());
  }


  @ApiOperation("发起流程")
  @PostMapping(value = "startFlow")
  public Result<String> startFlow(@RequestBody FormDataDto formDataDto) {
    return ResultUtil.success(flowServiceI.startProcessInstance(formDataDto, Constant.FLOW_START));
  }


  @ApiOperation("存草稿流程")
  @PostMapping(value = "notStartFlow")
  public Result<String> notStartFlow(@RequestBody FormDataDto formDataDto) {
    return ResultUtil.success(flowServiceI.notStartProcessInstance(formDataDto));
  }


  @ApiOperation("发起草稿流程")
  @PostMapping(value = "startDraftFlow")
  public Result<String> startDraftFlow(@RequestBody FormDataDto formDataDto) {
    return ResultUtil.success(flowServiceI.startDraftFlow(formDataDto));
  }


  @ApiOperation("获取我发起的、我已审批、待我审批的流程(当前登录人)")
  @PostMapping(value = "getMyFlows")
  public Result<Page<FlowOrderDto>> getMyFlows(@RequestBody QueryFlowDto queryFlowDto) {
    return ResultUtil.success(flowOrderServiceI.getMyFlows(queryFlowDto));
  }

  @ApiOperation("获取我的代办流程数(当前登录人)")
  @PostMapping(value = "getToApproveCount")
  public Result getToApproveCount() {
    return ResultUtil.success(flowOrderServiceI.getToApproveCount(SecurityUtils.currentUsername()));
  }


  @ApiOperation("获取我的代办、已办流程数(当前登录人)")
  @PostMapping(value = "getApproveCount")
  public Result<ApproveCountDto> getApproveCount() {
    return ResultUtil.success(flowOrderServiceI.getApproveCount());
  }


  @ApiOperation("获取具体的流程数据")
  @PostMapping(value = "getFlowById")
  public Result<FlowDataDto> getFlowById(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil.success(flowOrderServiceI.getFlowById(doFlowDto.getFlowId()));
  }


  @ApiOperation("审批同意流程")
  @PostMapping(value = "consentFlow")
  public Result<String> consentFlow(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil
        .success(flowServiceI.consentFlowById(doFlowDto.getFlowId(), doFlowDto.getMemo(), new Gson().toJson(doFlowDto.getFormDatas())));
  }

  @ApiOperation("审批转交流程")
  @PostMapping(value = "forwardFlow")
  public Result<String> forwardFlow(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil.success(flowServiceI.forwardFlowById(doFlowDto.getFlowId(),
        doFlowDto.getForwardUserId(), doFlowDto.getMemo(), new Gson().toJson(doFlowDto.getFormDatas())));
  }

  @ApiOperation("审批转交流程")
  @PostMapping(value = "hold")
  public Result<String> hold(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil.success(flowServiceI.forwardFlowById(doFlowDto.getFlowId(),
        SecurityUtils.getLoginUserId(), doFlowDto.getMemo(), new Gson().toJson(doFlowDto.getFormDatas())));
  }

  @ApiOperation("审批加签流程")
  @PostMapping(value = "counterSignFlow")
  public Result<String> counterSignFlow(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil.success(flowServiceI.counterSignFlowById(doFlowDto.getFlowId(),
        doFlowDto.getForwardUserId(), doFlowDto.getMemo(), new Gson().toJson(doFlowDto.getFormDatas())));
  }

  @ApiOperation("撤销流程")
  @PostMapping(value = "revokeFlow")
  public Result<String> revokeFlow(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil.success(flowServiceI.revokeProcessInstance(doFlowDto.getFlowId(), doFlowDto.getMemo()));
  }

  @ApiOperation("审批拒绝流程")
  @PostMapping(value = "rejectFlow")
  public Result<String> rejectFlow(@RequestBody DoFlowDto doFlowDto) {
    return ResultUtil
        .success(flowServiceI.rejectFlowById(doFlowDto.getFlowId(), doFlowDto.getMemo(), new Gson().toJson(doFlowDto.getFormDatas())));
  }

  @ApiOperation("催办")
  @PostMapping(value = "notifyNextApproveUsers")
  public Result<String> notifyNextApproveUsers(@RequestBody DoFlowDto doFlowDto) throws Exception {
    return ResultUtil.success(flowServiceI.notifyNextApproveUsers(doFlowDto.getFlowId()));
  }

  @ApiOperation("获取当前流程节点跟踪图")
  @PostMapping(value = "getFlowTraceImage")
  public Result<String> getFlowTraceImage(@RequestBody DoFlowDto doFlowDto) throws Exception {
    return ResultUtil.success(flowServiceI.getFlowTraceImage(doFlowDto.getFlowId()));
  }

  @ApiOperation("替换节点待审批人")
  @PostMapping(value ="updateNodeApproveUser")
  public Result<String> updateNodeApproveUser(@RequestBody FlowUserDto flowUserDto) {
    return ResultUtil.success(flowServiceI.updateNodeApproveUsers(flowUserDto.getFlowId(), flowUserDto.getOldUserId(), flowUserDto.getNewUserIds(), flowUserDto.getMemo(), flowUserDto.getOperationApprover()));
  }

}
