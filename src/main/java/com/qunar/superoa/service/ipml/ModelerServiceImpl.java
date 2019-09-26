package com.qunar.superoa.service.ipml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.dto.ModelDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.PageableDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.AdminActivitiServiceI;
import com.qunar.superoa.service.ModelerServiceI;
import com.qunar.superoa.utils.MinioUtils;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:42 PM 2019/3/29
 * @Modify by:
 */
@Slf4j
@Service
public class ModelerServiceImpl implements ModelerServiceI {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AdminActivitiServiceI adminActivitiServiceI;

  @Autowired
  private MinioUtils minioUtils;

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Override
  @Transactional
  public String newActivitiModel() {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    //初始化一个空模型
    Model model = repositoryService.newModel();

    //设置一些默认信息
    String name = "new-process";
    String description = "";
    int revision = 1;
    String key = "process";

    ObjectNode modelNode = objectMapper.createObjectNode();
    modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
    modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
    modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

    model.setName(name);
    model.setKey(key);
    model.setMetaInfo(modelNode.toString());

    repositoryService.saveModel(model);
    String id = model.getId();

    //完善ModelEditorSource
    ObjectNode editorNode = objectMapper.createObjectNode();
    editorNode.put("id", "canvas");
    editorNode.put("resourceId", "canvas");
    ObjectNode stencilSetNode = objectMapper.createObjectNode();
    stencilSetNode.put("namespace",
        "http://b3mn.org/stencilset/bpmn2.0#");
    editorNode.put("stencilset", stencilSetNode);
    repositoryService.addModelEditorSource(id,editorNode.toString().getBytes(StandardCharsets.UTF_8));

    return id;
  }

  @Override
  public PageResult<ModelDto> getModelList(PageAble pageAble) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    pageAble.setPage(pageAble.getPage() <= 0 ? 1 : pageAble.getPage());
    pageAble.setSize(pageAble.getSize() <= 0 ? 10 : pageAble.getSize());

    List<Model> modelList = Lists.newArrayList();
    PageResult<ModelDto> pageResult = new PageResult<>();
    //无搜索，分页向前端传递数据
    if (StringUtils.isBlank(pageAble.getK())) {
      modelList = repositoryService.createModelQuery().orderByLastUpdateTime().desc().listPage((pageAble.getPage() - 1) * pageAble.getSize(), pageAble.getSize());

      List<ModelDto> modelDtoList = Lists.newArrayList();
      modelList.forEach(model -> {
        ModelDto modelDto = new ModelDto(model);
        modelDtoList.add(modelDto);
      });
      pageResult.setContent(modelDtoList);
      pageResult.setTotal(repositoryService.createModelQuery().list().size());
    } else { //有搜索，先将数据查出,将数据全部传递向前端
      List<Model> modelListName = repositoryService.createModelQuery().modelNameLike("%" + pageAble.getK().trim() + "%").orderByLastUpdateTime().desc().list();
      List<Model> modelListId = repositoryService.createModelQuery().modelId(pageAble.getK().trim()).orderByLastUpdateTime().desc().list();
      List<Model> modelListKey = repositoryService.createModelQuery().modelKey(pageAble.getK().trim()).orderByLastUpdateTime().desc().list();
      modelList.addAll(modelListName);
      modelList.addAll(modelListId);
      modelList.addAll(modelListKey);
      List<ModelDto> modelDtoList = Lists.newArrayList();
      Set<String> modelIds = Sets.newHashSet();
      modelList.forEach(model -> {
        if (!modelIds.contains(model.getId())) {
          modelIds.add(model.getId());
          ModelDto modelDto = new ModelDto(model);
          modelDtoList.add(modelDto);
        }
      });
      pageResult.setContent(modelDtoList);
      pageResult.setTotal(modelDtoList.size());
    }
    pageResult.setPageable(new PageableDto(pageAble.getPage(), pageAble.getSize()));

    return pageResult;
  }

  @Override
  public String uploadBpmn(MultipartFile file) throws IOException {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    InputStream inputStream = file.getInputStream();
    BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
    XMLStreamReader xmlStreamReader;
    try {
      xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
    } catch (XMLStreamException e) {
      log.error("xml转换失败", e);
      e.printStackTrace();
      throw new FlowException(ResultEnum.ACTIVITI_MODEL_TO_XML_ERROR);
    }
    BpmnModel bpmnModel = xmlConverter.convertToBpmnModel(xmlStreamReader);
    BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
    Model model = repositoryService.newModel();
    List<Process> processes = bpmnModel.getProcesses();
    String name = "newProcess";
    String key = "process";
    Integer version = 1;
    if (processes.size() > 0) {
      if (StringUtils.isNotBlank(processes.get(0).getName())) {
        name = processes.get(0).getName();
      }
      if (StringUtils.isNotBlank(processes.get(0).getId())) {
        key = processes.get(0).getId();
      }
    }
    model.setName(name);
    model.setKey(key);
    List<Model> modelList = repositoryService.createModelQuery().modelKey(key).orderByModelVersion().desc().list();
    if (modelList.size() > 0) {
      version = modelList.get(0).getVersion() + 1;
    }
    model.setVersion(version);
    ObjectNode objectNode = objectMapper.createObjectNode();
    objectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
    objectNode.put(ModelDataJsonConstants.MODEL_REVISION, String.valueOf(version));

    model.setMetaInfo(objectNode.toString());
    repositoryService.saveModel(model);
    repositoryService.addModelEditorSource(model.getId(), modelNode.toString().getBytes(StandardCharsets.UTF_8));

    return name;
  }

  @Override
  public void downloadModel(String id, HttpServletResponse response) throws IOException {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return;
    }
    //根据数据库id获取model
    Model model = repositoryService.getModel(id);
    JsonNode editorNode;
    try {
      //获取核心数据
      editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(id));
    } catch (IOException e) {
      log.error("流程model转换为JsonNode失败", e);
      throw new FlowException(ResultEnum.ACTIVITI_MODEL_TO_JSONNODE_ERROR);
    }
    //JsonNode转换为BpmnNode
    BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(editorNode);

    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel, "utf-8");
    String fileName = model.getName() + ".bpmn20.xml";

    response.reset();
    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
    response.setCharacterEncoding("utf-8");
    response.setContentLength(bpmnBytes.length);
    response.setContentType("application/force-download;charset=utf-8");
    OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
    outputStream.write(bpmnBytes);
    outputStream.flush();
    outputStream.close();
  }

  @Override
  public void downBpmnModel(String processDefinitionId, HttpServletResponse response)
      throws IOException {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return;
    }
    //获取bpmnModel
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
    //bpmnModle转换为xml
    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel, "utf-8");
    //获取文件名
    String bpmnName = "newBpmnXMLDownload";
    List<Process> processList = bpmnModel.getProcesses();
    if (processList.size() > 0) {
      if (StringUtils.isNotBlank(processList.get(0).getName())) {
        bpmnName = processList.get(0).getName();
      }
    }
    String fileName = bpmnName + ".bpmn20.xml";

    //下载文件
    response.reset();
    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
    response.setCharacterEncoding("utf-8");
    response.setContentLength(bpmnBytes.length);
    response.setContentType("application/force-download;charset=utf-8");
    OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
    outputStream.write(bpmnBytes);
    outputStream.flush();
    outputStream.close();
  }

  @Override
  public String deployModel(String id) throws Exception {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    //获取模型
    Model modelData = repositoryService.getModel(id);
    byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

    if (bytes == null) {
      throw new FlowException(ResultEnum.ACTIVITI_MODEL_NULL);
    }

    JsonNode modelNode = new ObjectMapper().readTree(bytes);

    BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
    if(model.getProcesses().size()==0){
      throw new FlowException(ResultEnum.ACTIVITI_MODEL_ERROR);
    }
    byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

    //发布流程
    String processName = modelData.getName() + ".bpmn20.xml";
    Deployment deployment = repositoryService.createDeployment()
        .name(modelData.getName())
        .addString(processName, new String(bpmnBytes, StandardCharsets.UTF_8))
        .deploy();
    modelData.setDeploymentId(deployment.getId());
    repositoryService.saveModel(modelData);

    //根据部署id获取对应bpmnModel二进制流，并将URL和部署id存入数据库
    InputStream inputStream = adminActivitiServiceI.viewPic(deployment.getId());
    UserAttachment userAttachment = minioUtils.uploadImageToMinio(inputStream, deployment.getId());
    attachmentRepository.save(userAttachment);

    return processName;
  }

  @Override
  @Transactional
  public String deleteModel(String id) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    repositoryService.deleteModel(id);
    return "删除流程模型成功";
  }

}
