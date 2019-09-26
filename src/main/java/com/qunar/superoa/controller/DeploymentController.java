package com.qunar.superoa.controller;

import com.qunar.superoa.controller.common.RestServiceController;
import com.qunar.superoa.dto.DeploymentResponse;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.utils.ToWeb;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 11:37 AM 2019/3/26
 * @Modify by:
 */
@Api( value = "deployments", tags = "已部署流程的查询及删除")
@RestController
@RequestMapping("deployments")
public class DeploymentController implements RestServiceController<Deployment, String> {
  @Autowired
  RepositoryService repositoryService;

  @Override
  public Object getOne(@PathVariable("id") String id) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(id).singleResult();
    return ToWeb.buildResult().setObjData(new DeploymentResponse(deployment));
  }

  @Override
  public Object getList(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    List<Deployment> deployments = repositoryService.createDeploymentQuery()
        .listPage(rowSize * (page - 1), rowSize);
    long count = repositoryService.createDeploymentQuery().count();
    List<DeploymentResponse> list = new ArrayList<>();
    for(Deployment deployment: deployments){
      list.add(new DeploymentResponse(deployment));
    }

    return ToWeb.buildResult().setRows(
        ToWeb.Rows.buildRows()
            .setRowSize(rowSize)
            .setTotalPages((int) (count/rowSize+1))
            .setTotalRows(count)
            .setList(list)
            .setCurrent(page)
    );
  }

  @Override
  public Object deleteOne(@PathVariable("id") String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object postOne(@RequestBody Deployment entity) {
    return null;
  }

  @Override
  public Object putOne(@PathVariable("id") String s, @RequestBody Deployment entity) {
    return null;
  }

  @Override
  public Object patchOne(@PathVariable("id") String s, @RequestBody Deployment entity) {
    return null;
  }
}
