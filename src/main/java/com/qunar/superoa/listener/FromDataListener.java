package com.qunar.superoa.listener;

import com.google.gson.Gson;
import com.qunar.superoa.cache.Cache;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.FlowModelRepository;
import com.qunar.superoa.dao.FormDataRepository;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.model.FormData;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.JsonMapUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction: 通过流程模板和流程data获取当前节点的节点审批人
 * @Date:Created in 15:04 2018/11/7
 * @Modify by:
 *
 *  ${fromDataListener}
 */
@Component
public class FromDataListener extends CandidatesListener {

  @Autowired
  private FlowModelRepository flowModelRepository;

  @Autowired
  private FormDataRepository formDataRepository;

  /**
   * 重写设置节点操作着方法
   */
  @Override
  public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
    String processInstanceId = delegateExecution.getProcessInstanceId();
    List<String> candidateUsers = new ArrayList<>();
    FormData formData = formDataRepository.findByProcInstId(processInstanceId);
    FlowModel flowModel;
    String uuid = "";
    if (formData == null) {
      uuid = delegateExecution.getVariable(Constant.ACTIVITI_VARIABLE_KEY).toString();
      flowModel = (FlowModel) Cache.getFlowDataCacheByKey(uuid + "model");
    } else {
      flowModel = flowModelRepository.findById(formData.getFormModelId()).get();
    }
    String nodeApproveUsers = flowModel.getNodeApproveUsers();
    if (StringUtils.isNotBlank(nodeApproveUsers)) {
      Map<String, String> map = new Gson().fromJson(nodeApproveUsers, Map.class);
      String userNames;
      if (formData == null) {
        userNames = JsonMapUtil
            .getValueByKey(map.get(userTask.getName()), (Map<String, Object>) Cache.getFlowDataCacheByKey(uuid)).replaceAll("\\[", Constant.FILL_BLANK).replaceAll("]", Constant.FILL_BLANK);
      } else {
        userNames = JsonMapUtil
            .getValueByKey(map.get(userTask.getName()), CommonUtil.s2m(formData.getFormDatas())).replaceAll("\\[", Constant.FILL_BLANK).replaceAll("]", Constant.FILL_BLANK);
      }
      if(StringUtils.isBlank(userNames)){
        throw new FlowException(ResultEnum.FLOW_APPROVE_USERS_IS_NULL_ADD);
      }
      Arrays.stream(userNames.split(",")).forEach(username -> {
        if (!username.isEmpty()) {
          candidateUsers.add(username.replaceAll(Constant.FILL_SPACE, Constant.FILL_BLANK));
        }
      });
      userTask.setCandidateUsers(candidateUsers);
    } else {
      throw new FlowException(ResultEnum.FLOW_DATA_ERROR);
    }
  }
}
