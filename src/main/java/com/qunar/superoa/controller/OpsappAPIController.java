package com.qunar.superoa.controller;

import com.qunar.superoa.dto.FlowDataDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.model.OpsappAproveParams;
import com.qunar.superoa.model.OpsappModel;
import com.qunar.superoa.model.OpsappUpdateData;
import com.qunar.superoa.service.FlowOrderServiceI;
import com.qunar.superoa.service.FlowServiceI;
import com.qunar.superoa.service.OpsappApiServiceI;
import com.qunar.superoa.service.ipml.UserService;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: lee.guo
 * @Despriction: 开放给移动端接口
 * @Date: Created in 1:41 PM 2018/10/25
 * @Modify by:
 */
@Api(value = "OpsappAPIController", tags = "开放给移动端接口")
@Controller
@RequestMapping("/opsapp/api")
public class OpsappAPIController {

  @Autowired
  private FlowOrderServiceI flowOrderServiceI;
  @Autowired
  private FlowServiceI flowServiceI;
  @Autowired
  private OpsappApiServiceI opsappApiServiceI;
  @Autowired
  private UserService userDetailsService;

  @ApiOperation("同意操作接口")
  @PostMapping("approve")
  @ResponseBody
  public Result approve(@RequestBody OpsappAproveParams opsappAproveParams) {
    Arrays.asList(opsappAproveParams.getVars().getTaskIds().split(","))
        .forEach(taskID -> flowServiceI
            .consentFlowById(taskID,
                opsappAproveParams.getVars().getReason(), opsappAproveParams.getRtx_id(),
                userDetailsService.findUserCNameByName(opsappAproveParams.getRtx_id()), true, null,
                opsappAproveParams.getVars().getFormDatas()));
    return ResultUtil.success();
  }

  @ApiOperation("详情接口k:v")
  @PostMapping("details")
  @ResponseBody
  public Result<Map<String, String>> details(
      @RequestBody OpsappAproveParams opsappAproveParams) {
    return ResultUtil
        .success(flowServiceI.getFlowByIdFromApp(opsappAproveParams.getVars().getTaskIds()));
  }

  @ApiOperation("PC详情接口")
  @PostMapping("detailsAllInfo")
  @ResponseBody
  public Result<FlowDataDto> detailsAllInfo(
      @RequestBody OpsappAproveParams opsappAproveParams) {
    return ResultUtil.success(flowOrderServiceI
        .getFlowById(opsappAproveParams.getVars().getTaskIds(), opsappAproveParams.getRtx_id()));
  }

  @ApiOperation("历史接口")
  @PostMapping("history")
  @ResponseBody
  public Result<Map<String, String>> history(
      @RequestBody OpsappAproveParams opsappAproveParams) {
    return ResultUtil.success();
  }

  @ApiOperation("拒绝操作接口")
  @PostMapping("reject")
  @ResponseBody
  public Result reject(@RequestBody OpsappAproveParams opsappAproveParams) {
    Arrays.asList(opsappAproveParams.getVars().getTaskIds().split(","))
        .forEach(taskID -> flowServiceI
            .rejectFlowById(taskID,
                opsappAproveParams.getVars().getReason(), opsappAproveParams.getRtx_id(),
                userDetailsService.findUserCNameByName(opsappAproveParams.getRtx_id()), true,
                opsappAproveParams.getVars().getFormDatas()));
    return ResultUtil.success();
  }

  @ApiOperation("移除移动端待办接口")
  @PostMapping("removeByOids")
  @ResponseBody
  public Result removeByOids(
      @RequestBody Map params) {

    //1. 获取移除列表
    List<OpsappUpdateData> opsappUpdateDataList = new ArrayList();
    Arrays.stream(params.get("oid").toString().split(",")).forEach(oid -> {
      OpsappUpdateData opsappUpdateData = new OpsappUpdateData();
      List<String> arrayListpprover = new ArrayList();
      opsappUpdateData.setOid(oid);
      arrayListpprover.add("lee.guo");
      opsappUpdateData.setApprover(arrayListpprover);
      opsappUpdateData.setUser("lee.guo");
      opsappUpdateData.setTime("");
      opsappUpdateData.setSum("test");
      opsappUpdateDataList.add(opsappUpdateData);
    });
    //2. 推送数据
    return ResultUtil.success(CommonUtil
        .o2m(opsappApiServiceI.update_patch(new OpsappModel(opsappUpdateDataList, true))));
  }


}
