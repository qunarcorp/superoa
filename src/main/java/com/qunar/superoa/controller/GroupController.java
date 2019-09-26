package com.qunar.superoa.controller;

import com.qunar.superoa.dto.GroupIdDto;
import com.qunar.superoa.dto.QueryWorkGroupDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.model.WorkGroup;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.GroupServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "GroupController", tags = "工作组相关")
@RestController
@RequestMapping("/group")
public class GroupController {


  @Autowired
  private GroupServiceI groupServiceI;

  @ApiOperation("添加一个工作组")
  @PostMapping("/add")
  public Result add(@Valid @RequestBody WorkGroup workGroup, BindingResult bindingResult) throws Exception {
    return ResultUtil.validAndRun(bindingResult, () ->
      groupServiceI.saveWorkGroup(workGroup)
    );
  }


  @ApiOperation("根据ID查询")
  @PostMapping("/find")
  public WorkGroup findOne(@RequestBody GroupIdDto groupIdDto) {
    return groupServiceI.findWorkGroupById(groupIdDto.getId());
  }


  @PostMapping("/delete")
  @ApiOperation("根据ID删除")
  public Result delete(@RequestBody GroupIdDto groupIdDto) {
    groupServiceI.deleteWorkGroupById(groupIdDto.getId());
    return ResultUtil.success();
  }

  @ApiOperation("更新group")
  @PostMapping("/update")
  public Result update(@Valid @RequestBody WorkGroup workGroup, BindingResult bindingResult) throws Exception {
    return ResultUtil.validAndRun(bindingResult, () -> groupServiceI.updateWorkGroup(workGroup));
  }

  @ApiOperation("查询group列表")
  @PostMapping("/group")
  public Result list(@RequestBody QueryWorkGroupDto queryWorkGroupDto) {
    return ResultUtil.success(groupServiceI.findWorkGroupByLike(queryWorkGroupDto));
  }
}
