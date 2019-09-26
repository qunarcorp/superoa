package com.qunar.superoa.service;

import com.qunar.superoa.dto.ActivitiDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:32
 * @Despriction: 流程控制
 */
public interface AdminActivitiServiceI {

    String deploy(MultipartFile file) throws Exception;

    PageResult<ActivitiDto> processList(PageAble pageAble) ;

    ProcessDefinition processById(String deploymentId) ;

    ProcessDefinition processByKey(String procDefKey);

    String updateState(String deploymentId, String status) ;

    String delete(String deploymentId) ;

    String remove(String deploymentId) ;

    List findProcessInstanceList() throws Exception;

    InputStream readResource(String processDefinitionId, String processInstanceId) ;

    void loadByDeployment(String deploymentId, String resourceType, HttpServletResponse response) throws Exception;

    InputStream viewPic(String deploymentId);

    String viewPicForUrl(String deploymentId);

    List<ProcessDefinition> findProcessDefinition();
}
