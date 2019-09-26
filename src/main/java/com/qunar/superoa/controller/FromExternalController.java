package com.qunar.superoa.controller;

import com.qunar.superoa.dto.ExternalQueryDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowModelException;
import com.qunar.superoa.service.FromExternalServiceI;
import com.qunar.superoa.utils.MinioUtils;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhouxing
 * @date 2019-02-27 19:42
 */
@Slf4j
@Api(value = "外部系统接入接口", tags = "外部系统接入接口")
@RestController
@RequestMapping("fromExternal")
public class FromExternalController {

  @Autowired
  private FromExternalServiceI fromExternalServiceI;

  @Autowired
  private MinioUtils minioUtils;

  @ApiOperation("根据流程模板key查询流程模板")
  @PostMapping(value = "getFlowModel")
  public Result<FlowModelDto> getFlowModelByFormKey(@RequestBody ExternalQueryDto parameters) throws Exception {
    return ResultUtil.success(fromExternalServiceI.getFlowModelByFormKey(parameters.getFormKey(), parameters.getAppCode()));
  }

  @ApiOperation("发起流程")
  @PostMapping(value = "startFlow")
  public Result<FlowModelDto> startFlow(HttpServletRequest request, @RequestBody ExternalQueryDto parameters) throws Exception {

    return ResultUtil.success(fromExternalServiceI.startFlow(request, parameters));
  }

  @ApiOperation("上传附件")
  @PostMapping(value = "upload")
  public Result<FlowModelDto> upload(@RequestParam("file") MultipartFile file, @RequestParam("appCode") String appCode, @RequestParam("currentUserId") String currentUserId)
      throws Exception {
    log.info("appCode:{},userId:{}", appCode, currentUserId);
    if (StringUtils.isBlank(appCode)) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_APPCODE_NOT_NULL);
    }
    if (StringUtils.isBlank(currentUserId)) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_USER_NOT_NULL);
    }
    return ResultUtil.success(minioUtils.uploadFileToMinio(file));
  }
}
