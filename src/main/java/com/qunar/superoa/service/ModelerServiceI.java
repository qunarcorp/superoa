package com.qunar.superoa.service;

import com.qunar.superoa.dto.ModelDto;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.dto.PageResult;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction: activiti 流程设计相关方法
 * @Date:Created in 5:26 PM 2019/3/29
 * @Modify by:
 */
public interface ModelerServiceI {

  /**
   * 新建一个模型model
   */
  String newActivitiModel();

  /**
   * 上传bpmn20.xml文件并存入model数据库
   */
  String uploadBpmn(MultipartFile file) throws IOException;

  /**
   * 下载流程模型
   */
  void downloadModel(String id, HttpServletResponse response) throws IOException;

  /**
   * 下载已部署的bpmnModel
   */
  void downBpmnModel(String processDefinitionId, HttpServletResponse response)
      throws IOException;

  /**
   * 部署model模型
   */
  String deployModel(String id) throws Exception;

  /**
   * 获取model列表
   */
  PageResult<ModelDto> getModelList(PageAble pageAble);

  /**
   * 删除model
   */
  String deleteModel(String id);

}
