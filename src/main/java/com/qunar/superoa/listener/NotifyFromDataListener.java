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
import org.activiti.bpmn.model.ManualTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: xing.zhou
 * @Despriction: 从表单获取知会人
 * @Date:Created in 19:27 2019/1/7
 * @Modify by:
 */
@Component
public class NotifyFromDataListener extends CandidatesListener {

  @Autowired
  private FlowModelRepository flowModelRepository;

  @Autowired
  private FormDataRepository formDataRepository;

  /**
   * 重写设置知会人方法
   */
  @Override
  public void sendNotify(ManualTask manualTask, DelegateExecution delegateExecution) {
    String processInstanceId = delegateExecution.getProcessInstanceId();
    StringBuffer candidateUsers = new StringBuffer();
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
            .getValueByKey(map.get(manualTask.getName()), (Map<String, Object>) Cache.getFlowDataCacheByKey(uuid)).replaceAll("\\[", Constant.FILL_BLANK).replaceAll("]", Constant.FILL_BLANK);
      } else {
        userNames = JsonMapUtil
            .getValueByKey(map.get(manualTask.getName()), CommonUtil.s2m(formData.getFormDatas())).replaceAll("\\[", Constant.FILL_BLANK).replaceAll("]", Constant.FILL_BLANK);
      }
      Arrays.stream(userNames.split(",")).forEach(username -> {
        if (!username.isEmpty()) {
          candidateUsers.append(username.replaceAll(Constant.FILL_SPACE, Constant.FILL_BLANK)).append(",");
        }
      });

      //知会查看人字段
      Object temp = Cache.getFlowDataCacheByKey(processInstanceId);
      Cache.setFlowDataCache(processInstanceId,
          temp != null && StringUtils.isNotBlank(temp.toString()) ? temp.toString() + candidateUsers.toString() : candidateUsers.toString());
    } else {
      throw new FlowException(ResultEnum.FLOW_DATA_ERROR);
    }
  }
}