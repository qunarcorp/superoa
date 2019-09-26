package com.qunar.superoa.controller;

import com.qunar.superoa.dao.AgentRepository;
import com.qunar.superoa.dto.*;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.AgentException;
import com.qunar.superoa.model.Agent;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.AgentServiceI;
import com.qunar.superoa.utils.DateTimeUtil;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/18_下午6:14
 * @Despriction:
 */

@Slf4j
@Api(value = "AgentController", tags = "代理人相关")
@RestController
@RequestMapping("agent")
public class AgentController {

  @Autowired
  private AgentServiceI agentServiceI;

  @Autowired
  private AgentRepository agentRepository;

  @ApiOperation("添加代理人")
  @PostMapping("add")
  public Result<AgentDto> add(@RequestBody @Valid Agent agent, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      if (bindingResult.getFieldError().getDefaultMessage().indexOf("Failed to convert") != -1 ||
          bindingResult.getFieldError().getDefaultMessage().indexOf("deadline") != -1) //时间格式错误
      {
        throw new AgentException(ResultEnum.DATE_FORMART_ERROR);
      }
    }
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return ResultUtil.error(405, "无管理员权限");
    }
    agent.setUpdateTime(DateTimeUtil.currentTime());
    return ResultUtil.validAndRun(bindingResult, () -> new AgentDto(agentRepository.save(agent)));
  }


  @ApiOperation("根据ID查询")
  @PostMapping("/find")
  public Result<AgentDto> findOne(@RequestBody Agent agent) {
    return ResultUtil.success(new AgentDto(agentRepository.findById(agent.getId()).get()));
  }


  @PostMapping("/delete")
  @ApiOperation("根据ID删除")
  public Result<String> delete(@RequestBody Agent agent) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return ResultUtil.error(405, "无管理员权限");
    }
    agentRepository.deleteById(agent.getId());
    return ResultUtil.success();
  }

  @ApiOperation("更新代理人信息")
  @PostMapping("/update")
  public Result<AgentDto> update(@RequestBody @Valid Agent agent, BindingResult bindingResult)
      throws Exception {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return ResultUtil.error(405, "无管理员权限");
    }
    return add(agent, bindingResult);
  }

  @ApiOperation("查询代理人列表")
  @PostMapping("list")
  public Result<PageResult<AgentDto>> list(@RequestBody PageAble pageAble) {
    //将MODEL转为DTO并返回
    //这里为了解决时间的问题，所以重新封装了PAGE对象(PageResult)。  正常可以直接返回PAGE。
    return ResultUtil.success(agentServiceI.getListPageAble(pageAble));
  }
}
