package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dto.ExternalFlowModelDto;
import com.qunar.superoa.dto.ExternalQueryDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.ACLManageException;
import com.qunar.superoa.exceptions.FlowModelException;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.service.ACLManageServiceI;
import com.qunar.superoa.service.FlowModelServiceI;
import com.qunar.superoa.service.FlowServiceI;
import com.qunar.superoa.service.FromExternalServiceI;
import com.qunar.superoa.utils.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouxing
 * @date 2019-02-27 20:58
 */
@Service
@Slf4j
public class FromExternalServiceImpl implements FromExternalServiceI {

  @Autowired
  private FlowModelServiceI flowModelServiceI;

  @Autowired
  private FlowServiceI flowServiceI;

  @Autowired
  private UserService userDetailsService;

  @Autowired
  private ACLManageServiceI aclManageServiceI;

  @Override
  public ExternalFlowModelDto getFlowModelByFormKey(String formKey, String appCode) {
    log.info("appCode:{},formKey:{}", appCode, formKey);
    if (StringUtils.isBlank(formKey)) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_FLOW_KEY_NOT_NULL);
    }
    if (StringUtils.isBlank(appCode)) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_APPCODE_NOT_NULL);
    }
    FlowModel flowModel = flowModelServiceI.getFlowModelByFormKey(formKey);
    if (flowModel == null) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NOT_EXISTS);
    } else {
      return new ExternalFlowModelDto(flowModel);
    }
  }

  @Override
  public String startFlow(HttpServletRequest request, ExternalQueryDto externalQueryDto) {
    String ip = NetworkUtil.getIpAddress(request);
    log.info("userId:{},appCode:{},formKey:{},ip:{}", externalQueryDto.getCurrentUserId(), externalQueryDto.getAppCode(), externalQueryDto.getFormKey(), ip);
    //formKey 必填
    if (StringUtils.isBlank(externalQueryDto.getFormKey())) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_FLOW_KEY_NOT_NULL);
    }
    //appCode 必填
    if (StringUtils.isBlank(externalQueryDto.getAppCode())) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_APPCODE_NOT_NULL);
    }
    //版本号必填
    if (StringUtils.isBlank(externalQueryDto.getVersion())) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_VERSION_NOT_NULL);
    }
    //发起人 必填
    if (StringUtils.isBlank(externalQueryDto.getCurrentUserId())) {
      throw new FlowModelException(ResultEnum.FLOW_ORDER_USER_NOT_NULL);
    }
    //验证是否有该流程权限
    String flows = aclManageServiceI.getAclManageByIp(ip).get().getFlows();
    if (!"all".equalsIgnoreCase(flows) && !("," + flows + ",").contains("," + externalQueryDto.getFormKey() + ",")) {
      throw new ACLManageException(ResultEnum.ACL_MANAGE_FLOW_NULL);
    }
    if (StringUtils.isBlank(externalQueryDto.getCurrentUserName())) {
      externalQueryDto.setCurrentUserName(userDetailsService.findUserCNameByName(externalQueryDto.getCurrentUserId()));
    }
    return flowServiceI.startFromExternal(externalQueryDto, ip);
  }

}
