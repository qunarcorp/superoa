package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.dto.ActivitiDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.PageableDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.ActivitiFlowException;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.AdminActivitiServiceI;
import com.qunar.superoa.utils.DateTimeUtil;
import com.qunar.superoa.utils.MinioUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:32
 * @Despriction: 流程控制
 */

@Slf4j
@Service
public class AdminActivitiServiceImpl implements AdminActivitiServiceI {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private ProcessDiagramGenerator processDiagramGenerator;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private MinioUtils minioUtils;

  ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

  @Override
  public String deploy(MultipartFile file) throws Exception {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    String fileName = file.getOriginalFilename();
    InputStream fileInputStream = file.getInputStream();
    Deployment deployment = null;
    String extension = FilenameUtils.getExtension(fileName);
    DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
    if (Constant.FILE_TYPE_ZIP.equals(extension) || Constant.FILE_TYPE_BAR.equals(extension)) {
      ZipInputStream zip = new ZipInputStream(fileInputStream);
      deployment = deploymentBuilder.addZipInputStream(zip).deploy();
    } else if (Constant.FILE_TYPE_BPMN.equalsIgnoreCase(extension)
        || Constant.FILE_TYPE_XML.equalsIgnoreCase(extension)) {
      deployment = deploymentBuilder.addInputStream(fileName, fileInputStream).deploy();
    }

    //将部署流程二进制图片上传到服务器，并将URL和deploymentId存入数据库
    InputStream inputStream = viewPic(deployment.getId());
    UserAttachment userAttachment = minioUtils.uploadImageToMinio(inputStream, deployment.getId());
    attachmentRepository.save(userAttachment);
    log.info("部署ID：" + deployment.getId());
    log.info("部署名称:" + deployment.getName());
    String processDefinitionId = repositoryService.createProcessDefinitionQuery()
        .deploymentId(deployment.getId()).singleResult().getId();
    repositoryService.createProcessDefinitionQuery().processDefinitionId(deployment.getId())
        .list().forEach(
        processDefinition -> {
          if (!processDefinition.getId().equals(processDefinitionId)) {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId());
          }
        }
    );
    return deployment.getId();
  }

  @Override
  public List findProcessInstanceList() throws Exception {
    return null;
  }

  @Override
  public PageResult<ActivitiDto> processList(PageAble pageAble) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    pageAble.setPage(pageAble.getPage() <= 0 ? 1 : pageAble.getPage());
    pageAble.setSize(pageAble.getSize() <= 0 ? 10 : pageAble.getSize());
    List<ProcessDefinition> processDefinitions = Lists.newArrayList();
    PageResult<ActivitiDto> pageResult = new PageResult<>();
    //无搜索，分页向前端传递数据
    if (StringUtils.isBlank(pageAble.getK())) {
      processDefinitions = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionKey()
          .desc().listPage((pageAble.getPage() - 1) * pageAble.getSize(), pageAble.getSize());
      List<ActivitiDto> activitiDtos = new ArrayList<>();
      processDefinitions.forEach(processDefinition -> {
        ActivitiDto activitiDto = getActivitiDto(processDefinition);
        activitiDtos.add(activitiDto);
      });
      pageResult.setContent(activitiDtos);
      pageResult.setTotal(repositoryService.createProcessDefinitionQuery().list().size());
    } else { //有搜索，查出全部后，做筛选处理
      List<ProcessDefinition> processDefinitionsName = repositoryService.createProcessDefinitionQuery().processDefinitionNameLike("%" + pageAble.getK().trim() + "%").orderByProcessDefinitionKey().desc().list();
      List<ProcessDefinition> processDefinitionsDeploymentId = repositoryService.createProcessDefinitionQuery().deploymentId(pageAble.getK().trim()).orderByProcessDefinitionKey().desc().list();
      List<ProcessDefinition> processDefinitionsKey = repositoryService.createProcessDefinitionQuery().processDefinitionKey(pageAble.getK().trim()).orderByProcessDefinitionKey().desc().list();
      processDefinitions.addAll(processDefinitionsName);
      processDefinitions.addAll(processDefinitionsKey);
      processDefinitions.addAll(processDefinitionsDeploymentId);
      List<ActivitiDto> activitiDtos = new ArrayList<>();
      Set<String> deploymentIds = Sets.newHashSet();
      processDefinitions.forEach(processDefinition -> {
        //去重
        if (!deploymentIds.contains(processDefinition.getDeploymentId())) {
          deploymentIds.add(processDefinition.getDeploymentId());
          ActivitiDto activitiDto = getActivitiDto(processDefinition);
          activitiDtos.add(activitiDto);
        }
      });
      pageResult.setContent(activitiDtos);
      pageResult.setTotal(activitiDtos.size());
    }
    pageResult.setPageable(new PageableDto(pageAble.getPage(), pageAble.getSize()));
    return pageResult;
  }

  /**
   * 根据流程定义获取activitiDto - 前端传值
   */
  private ActivitiDto getActivitiDto(ProcessDefinition processDefinition) {
    ActivitiDto activitiDto = new ActivitiDto();
    activitiDto.setDeploymentId(processDefinition.getDeploymentId());
    activitiDto.setProcessDefinitionId(processDefinition.getId());
    activitiDto.setName(processDefinition.getName());
    activitiDto.setKey(processDefinition.getKey());
    activitiDto.setDeployDateTime(DateTimeUtil.toString(repositoryService.createDeploymentQuery()
        .deploymentId(processDefinition.getDeploymentId()).singleResult().getDeploymentTime()));
    activitiDto.setStatus(processDefinition.isSuspended() ? Constant.ACTIVITI_SUSPEND_STATUS
        : Constant.ACTIVITI_ACTIVE_STATUS);
    activitiDto.setFlowCount(historyService.createHistoricProcessInstanceQuery()
        .processDefinitionId(processDefinition.getId()).count());
    activitiDto.setVersion(String.valueOf(processDefinition.getVersion()));
    activitiDto.setFlowUnFinishedCount(
        taskService.createTaskQuery().processDefinitionId(processDefinition.getId()).count());
    activitiDto.setImgName(processDefinition.getDiagramResourceName() == null ? checkImageInAttatchment(processDefinition.getDeploymentId() + ".svg")
        : processDefinition.getDiagramResourceName());
    return activitiDto;
  }

  /**
   * 根据文件名判断附件表中是否存在
   */
  private String checkImageInAttatchment( String fileName ) {
    Optional<UserAttachment> userAttachmentOptional = attachmentRepository.findByAttachName(fileName);
    return userAttachmentOptional.isPresent() ? fileName : "";
  }

  @Override
  public ProcessDefinition processById(String deploymentId) {
    return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId)
        .singleResult();
  }

  @Override
  public ProcessDefinition processByKey(String procDefKey) {
    return repositoryService.createProcessDefinitionQuery().processDefinitionKey(procDefKey)
        .singleResult();
  }

  @Override
  public String updateState(String deploymentId, String status) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
        .deploymentId(deploymentId).singleResult();
    if (processDefinition == null) {
      throw new ActivitiFlowException(ResultEnum.ACTIVITI_FLOW_NOT);
    } else {
      //启动
      if ("1".equals(status)) {
        if (processDefinition.isSuspended()) {
          repositoryService.activateProcessDefinitionById(processDefinition.getId());
          return "启动成功";
        } else {
          return "已启动状态";
        }
      } else if ("0".equals(status)) {
        if (!processDefinition.isSuspended()) {
          repositoryService.suspendProcessDefinitionById(processDefinition.getId());
          return "挂起成功";
        } else {
          return "已挂起状态";
        }
      }
    }
    return null;
  }

  @Override
  public String delete(String deploymentId) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    //不删除集联  即 不删除发起的流程实例
    return deleteDeployment(deploymentId, false);
  }

  @Override
  public String remove(String deploymentId) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    //删除集联  即 删除发起的流程实例
    return deleteDeployment(deploymentId, true);
  }

  /**
   * 删除部署的流程实例
   */
  public String deleteDeployment(String deploymentId, boolean cascade) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return "无管理员权限";
    }
    List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
        .deploymentId(deploymentId).list();
    if (processDefinitionList == null || processDefinitionList.size() == 0) {
      throw new ActivitiFlowException(ResultEnum.ACTIVITI_FLOW_NOT);
    } else {
      repositoryService.deleteDeployment(deploymentId, cascade);
      processDefinitionList = repositoryService.createProcessDefinitionQuery()
          .deploymentId(deploymentId).list();
      if (processDefinitionList == null || processDefinitionList.size() == 0) {
        return "该流程移除成功";
      } else {
        throw new ActivitiFlowException(ResultEnum.ACTIVITI_FLOW_DELETE_ERROR);
      }
    }
  }

  @Override
  public InputStream readResource(String processDefinitionId, String processInstanceId) {
//        BpmnModel bpmnModel = repositoryService
//                .getBpmnModel(processDefinitionId);
//        List<Task> list = taskService.createTaskQuery().active()
//                .processInstanceId(processInstanceId).list();
//        List<String> activeActivityIds = new ArrayList<String>();
//        for (Task task : list) {
//            activeActivityIds.add(task.getTaskDefinitionKey());
//        }
//        Context.setProcessEngineConfiguration(processEngineFactoryBean
//                .getProcessEngineConfiguration());
//        return this.processEngineFactoryBean
//                .getProcessEngineConfiguration().getProcessDiagramGenerator()
//                .generateDiagram(bpmnModel, "png", activeActivityIds,
//                        Collections.<String>emptyList(),
//                        processEngineFactoryBean.getProcessEngineConfiguration().getActivityFontName(),
//                        processEngineFactoryBean.getProcessEngineConfiguration().getLabelFontName(),
//                        null, 1.0);
    return null;
  }

  @Override
  public void loadByDeployment(String deploymentId, String resourceType,
      HttpServletResponse response) throws Exception {
    ProcessDefinition processDefinition = repositoryService
        .createProcessDefinitionQuery()
        .deploymentId(deploymentId).singleResult();
    String resourceName = "";
    if (resourceType.equals("image")) {
      resourceName = processDefinition.getDiagramResourceName();
    } else if (resourceType.equals("xml")) {
      resourceName = processDefinition.getResourceName();
    }
    InputStream resourceAsStream = repositoryService.getResourceAsStream(
        processDefinition.getDeploymentId(), resourceName);
    byte[] b = new byte[1024];
    int len = -1;
    while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
      response.getOutputStream().write(b, 0, len);
    }
  }

  /**
   * 查询流程定义
   */
  @Override
  public List<ProcessDefinition> findProcessDefinition() {
    //与流程定义和部署对象相关的Service
    List<ProcessDefinition> list = processEngine.getRepositoryService()
        .createProcessDefinitionQuery()//创建一个流程定义查询
        /*指定查询条件,where条件*/
        //.deploymentId(deploymentId)//使用部署对象ID查询
        //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
        //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
        //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

        /*排序*/
        .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
        //.orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

        .list();//返回一个集合列表，封装流程定义
    //.singleResult();//返回唯一结果集
    //.count();//返回结果集数量
    //.listPage(firstResult, maxResults)//分页查询

    return list;

  }


  /**
   * 查询流程图并返回流
   */
  @Override
  public InputStream viewPic(String deploymentId) {
    /**将生成图片放到文件夹下*/
    //获取图片资源名称
    List<String> list = processEngine.getRepositoryService()
        .getDeploymentResourceNames(deploymentId);
    if (list == null || list.isEmpty()) {
      throw new ActivitiFlowException(ResultEnum.ACTIVITI_FLOW_NOT);
    }
    //定义图片资源的名称
    String resourceName = "";
    if (list != null && list.size() > 0) {
      for (String name : list) {
        if (name.contains(".png") && !name.contains("_")) {
          resourceName = name;
        }
      }
    }
    //上传流程图为空时自动生成流程图
    if (resourceName.isEmpty()) {
      //throw new ActivitiFlowException(ResultEnum.ACTIVITI_FLOW_IMG_NOT);
      String processDefinitionId = processEngine.getRepositoryService()
          .createProcessDefinitionQuery().deploymentId(deploymentId)
          .orderByProcessDefinitionVersion().desc().list().get(0).getId();
      BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

      return processDiagramGenerator.generateDiagram(bpmnModel, "simsun", "simsun", "simsun");
    }

    //获取图片的输入流
    return processEngine.getRepositoryService().getResourceAsStream(deploymentId, resourceName);
  }

  /**
   * 查询流程图并返回url
   */
  @Override
  public String viewPicForUrl(String deploymentId) {
    return attachmentRepository.findByRelationId(deploymentId).getUrl();
  }
}
