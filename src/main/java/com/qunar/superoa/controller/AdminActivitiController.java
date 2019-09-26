package com.qunar.superoa.controller;

import com.qunar.superoa.dto.ActivitiDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.service.AdminActivitiServiceI;
import com.qunar.superoa.utils.MinioUtils;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:22
 * @Despriction: admin工作流管理相关
 */

@Slf4j
@Api(value = "AdminActivitiController", tags =  "admin工作流管理")
@RestController
@RequestMapping("admin")
public class AdminActivitiController {

  @Autowired
  private AdminActivitiServiceI adminActivitiServiceI;

  @Autowired
  private MinioUtils minioUtils;

  @ApiOperation(value = "部署流程", notes = "部署流程")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "file", value = "bpmn流程文件", required = true, dataType = "MultipartFile")
  })
  @PostMapping("deploy")
  public Result<String> deploy(@RequestParam("file") MultipartFile file) throws Exception {
    return ResultUtil.run(() -> adminActivitiServiceI.deploy(file));
  }

  @ApiOperation("流程列表")
  @PostMapping("processList")
  public Result<Page<ActivitiDto>> processList(@RequestBody PageAble pageAble) {
    return ResultUtil.successForGson(adminActivitiServiceI.processList(pageAble));
  }

  @ApiOperation("指定id的流程")
  @PostMapping("processById")
  public Result<ProcessDefinition> processById(
      @ApiParam(name = "deploymentId", value = "流程部署Id", required = true) @RequestParam("deploymentId") String deploymentId) {
    return ResultUtil.successForGson(adminActivitiServiceI.processById(deploymentId));
  }

  @ApiOperation("激活、挂起流程")
  @PostMapping("updateState")
  public Result<String> updateState(@RequestBody ActivitiDto activitiDto) {
    return ResultUtil.successForGson(
        adminActivitiServiceI.updateState(activitiDto.getDeploymentId(), activitiDto.getStatus()));
  }

  @ApiOperation("删除流程定义")
  @PostMapping("delete")
  public Result<String> delete(@RequestBody ActivitiDto activitiDto) {
    return ResultUtil.successForGson(adminActivitiServiceI.delete(activitiDto.getDeploymentId()));
  }

  @ApiOperation("移除流程")
  @PostMapping("remove")
  public Result<String> remove(
      @ApiParam(name = "deploymentId", value = "流程部署Id", required = true) @RequestParam("deploymentId") String deploymentId) {
    return ResultUtil.successForGson(adminActivitiServiceI.remove(deploymentId));
  }

  @ApiOperation("流程任务跟踪图")
  @PostMapping("readResource")
  public void readResource(@RequestParam("processDefinitionId") String processDefinitionId,
      @RequestParam("processInstanceId") String processInstanceId,
      HttpServletResponse response) throws Exception {
    InputStream imageStream = adminActivitiServiceI
        .readResource(processDefinitionId, processInstanceId);
    byte[] b = new byte[1024];
    int len;
    while ((len = imageStream.read(b, 0, 1024)) != -1) {
      response.getOutputStream().write(b, 0, len);
    }

  }

  @ApiOperation("流程实例中任务列表")
  @PostMapping("findProcessInstanceList")
  public Result findProcessInstanceList() throws Exception {
    return ResultUtil.run(() -> adminActivitiServiceI.findProcessInstanceList());
  }

  @ApiOperation("查看流程定义资源文件")
  @PostMapping("loadByDeployment")
  public void loadByDeployment(
      @ApiParam(name = "deploymentId", value = "流程部署Id", required = true) @RequestParam("deploymentId") String deploymentId,
      @ApiParam(name = "resourceType", value = "源文件类型", required = true) @RequestParam("resourceType") String resourceType,
      HttpServletResponse response) throws Exception {
    adminActivitiServiceI.loadByDeployment(deploymentId, resourceType, response);
  }

  @ApiOperation("查询流程图，返回二进制流")
  @PostMapping("viewPic")
  public void viewPic(@RequestBody ActivitiDto activitiDto,
      HttpServletResponse response) throws IOException {
    InputStream imageStream = adminActivitiServiceI.viewPic(activitiDto.getDeploymentId());
    byte[] b = new byte[1024];
    int len;
    while ((len = imageStream.read(b, 0, 1024)) != -1) {
      response.getOutputStream().write(b, 0, len);
    }
  }

  @ApiOperation("查询流程图，返回url")
  @PostMapping("viewPicForURL")
  public Result viewPicForURL(@RequestBody ActivitiDto activitiDto) {
    String url = adminActivitiServiceI.viewPicForUrl(activitiDto.getDeploymentId());
    return ResultUtil.success(url);
  }


  @ApiOperation("测试获取指定流程的图片")
  @GetMapping("viewPicTest")
  public void viewPicTest(HttpServletResponse response) throws IOException {
    InputStream imageStream = adminActivitiServiceI.viewPic("4bd08a1b-b020-11e8-aa37-feac194325c3");
    byte[] b = new byte[1024];
    int len;
    while ((len = imageStream.read(b, 0, 1024)) != -1) {
      response.getOutputStream().write(b, 0, len);
    }
  }

  @ApiOperation("流程定义列表")
  @GetMapping("findProcessDefinition")
  public List<ProcessDefinition> findProcessDefinition() {
    return adminActivitiServiceI.findProcessDefinition();
  }

  @ApiOperation(value = "上传附件", notes = "上传附件")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "file", value = "bpmn流程文件", required = true, dataType = "MultipartFile"),
  })
  @PostMapping("upload")
  public Result<UserAttachment> upload(@RequestParam("file") MultipartFile file) throws Exception {
    return ResultUtil.success(minioUtils.uploadFileToMinio(file));
  }
}
