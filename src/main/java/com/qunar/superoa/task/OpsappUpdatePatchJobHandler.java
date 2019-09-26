package com.qunar.superoa.task;

import com.qunar.superoa.model.OpsappModel;
import com.qunar.superoa.model.OpsappUpdateData;
import com.qunar.superoa.service.FlowServiceI;
import com.qunar.superoa.service.OpsappApiServiceI;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.TaskLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: lee.guo
 * @Despriction: 推送待办定时任务
 * @Date: Created in 5:57 PM 2018/10/29
 * @Modify by:
 */
@JobHandler(value = "opsappUpdatePatchJobHandler")
@Component
public class OpsappUpdatePatchJobHandler extends IJobHandler {

  @Autowired
  private OpsappApiServiceI opsappApiServiceI;

  @Autowired
  private FlowServiceI flowServiceI;

  @Override
  public ReturnT<String> execute(String s) {
    TaskLogger.info("开始增量推送待办列表");
    List<OpsappUpdateData> opsappUpdateDataList = new ArrayList();
    //1. 获取待办列表
    flowServiceI.getTodos(1)
        .ifPresent(flowOrders -> flowOrders
            .forEach(flowOrder -> {
              OpsappUpdateData opsappUpdateData = new OpsappUpdateData(flowOrder);
              if (opsappUpdateData.getApprover().size() != 0) {
                opsappUpdateDataList.add(opsappUpdateData);
              }
            }));
    //2. 推送数据
    Map<String, Object> result = CommonUtil
        .o2m(opsappApiServiceI.update_patch(new OpsappModel(opsappUpdateDataList, true)));
    TaskLogger.info(result.toString());
    if (result.isEmpty()) {
      TaskLogger.error("增量推送待办的接口异常，请排查原因！");
      return FAIL;
    } else if ("0.0".equalsIgnoreCase(result.get("errcode").toString())) {
      TaskLogger.info("增量推送待办成功！共{}条~", opsappUpdateDataList.size());
      return SUCCESS;
    } else {
      TaskLogger.error("增量推送待办发生错误！");
      return FAIL;
    }
  }
}

