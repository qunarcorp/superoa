package com.qunar.superoa.controller;

import com.qunar.superoa.controller.common.RestServiceController;
import com.qunar.superoa.dto.ModelDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.ModelerServiceI;
import com.qunar.superoa.utils.ResultUtil;
import com.qunar.superoa.utils.ToWeb;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:  activiti流程model操作(编辑、部署、获取、删除)
 * @Date:Created in 11:31 AM 2019/3/26
 * @Modify by:
 */
@Api(value = "modelerController", tags = "activiti流程model操作(编辑、部署、获取、删除)")
@RestController
@RequestMapping("/models")
@Slf4j
public class ModelerController implements RestServiceController<Model, String> {

  @Autowired
  private ModelerServiceI modelerServiceI;

  @Autowired
  private RepositoryService repositoryService;

  /**
   * 新建一个空模型model
   * @return 重定向到activiti编辑器
   */
  @PostMapping("/newModel")
  public Result<String> newModel() {
    String id = modelerServiceI.newActivitiModel();
    return ResultUtil.success("/editor?modelId="+id);
  }

  /**
   * 将bpmn20.xml文件存入model数据库
   */
  @PostMapping("/uploadBpmn")
  public Object uploadBpmn(@RequestParam("file") MultipartFile file) throws IOException {
    String processName = modelerServiceI.uploadBpmn(file);
    return ResultUtil.success(processName);
  }

  /**
   * 发布模型为流程定义
   * @param map model模型id json
   */
  @PostMapping("/deployModel")
  public Object deploy(@RequestBody Map<String, String> map) throws Exception {
    String processName = modelerServiceI.deployModel(map.get("id"));
    return ResultUtil.success(processName);
  }

  /**
   * 下载流程模型
   */
  @GetMapping("/{id}/downloadModel")
  public void downloadModel(@PathVariable(name = "id") String id, HttpServletResponse response) throws IOException {
    modelerServiceI.downloadModel(id, response);
  }

  /**
   * 下载已部署的bpmn流程模型
   * @param processDefinitionId 流程定义id
   */
  @GetMapping("/{processDefinitionId}/downloadBpmnModel")
  public void downloadBpmnModel(@PathVariable(name = "processDefinitionId") String processDefinitionId, HttpServletResponse response) throws IOException {
    modelerServiceI.downBpmnModel(processDefinitionId, response);
  }

  /**
   * 获取model列表分页
   * @return
   */
  @PostMapping("/getModelList")
  public Result<Page<ModelDto>> getModelList(@RequestBody PageAble pageAble) {
    return ResultUtil.successForGson(modelerServiceI.getModelList(pageAble));
  }

  /**
   * 删除一个模型
   * @param map 资源的唯一标识的json
   * @return 是否成功标识
   */
  @PostMapping("/deleteModel")
  public Result<String> deleteModel(@RequestBody Map<String, String> map) {
    return ResultUtil.successForGson(modelerServiceI.deleteModel(map.get("id")));
  }

  @Override
  public Object getOne(@PathVariable("id") String id) {
    Model model = repositoryService.createModelQuery().modelId(id).singleResult();
    return ToWeb.buildResult().setObjData(model);
  }

  @Override
  public Object getList(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
    List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1)
        , rowSize);
    long count = repositoryService.createModelQuery().count();

    return ToWeb.buildResult().setRows(
        ToWeb.Rows.buildRows().setCurrent(page)
            .setTotalPages((int) (count/rowSize+1))
            .setTotalRows(count)
            .setList(list)
            .setRowSize(rowSize)
    );
  }

  @Override
  public Object deleteOne(@PathVariable("id")String id){
    throw new UnsupportedOperationException();
  }

  @Override
  public Object postOne(@RequestBody Model entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object putOne(@PathVariable("id") String s, @RequestBody Model entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object patchOne(@PathVariable("id") String s, @RequestBody Model entity) {
    throw new UnsupportedOperationException();
  }


}
