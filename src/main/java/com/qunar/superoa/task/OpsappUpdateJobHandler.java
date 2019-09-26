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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Auther: lee.guo
 * @Despriction: 推送待办定时任务
 * @Date: Created in 5:57 PM 2018/10/29
 * @Modify by:
 */
@JobHandler(value = "opsappUpdateJobHandler")
@Component
public class OpsappUpdateJobHandler extends IJobHandler {

  @Autowired
  private OpsappApiServiceI opsappApiServiceI;

  @Autowired
  private FlowServiceI flowServiceI;

  @Value("${api.opsapp}")
  private String opsappURL;

  @Override
  public ReturnT<String> execute(String s) {
    TaskLogger.info("开始推送待办列表:{}", opsappURL);
    TaskLogger.info("参数:{}", s);
    //s:1 不推送可编辑节点floworder s:0 推送全部

    opsappApiServiceI.update_patch(new OpsappModel(new ArrayList(), true));

    List<OpsappUpdateData> opsappUpdateDataList = new ArrayList();
    //1. 获取待办列表
    flowServiceI.getTodos(1)
        .ifPresent(flowOrders -> flowOrders
            .forEach(flowOrder -> {
              //判断当前floworder是否在编辑节点上
              if ("0".equals(s) || !flowServiceI.inExitNode(flowOrder)) {
                OpsappUpdateData opsappUpdateData = new OpsappUpdateData(flowOrder);
                if (opsappUpdateData.getApprover().size() != 0) {
                  opsappUpdateDataList.add(opsappUpdateData);
                }
              }
            }));
    //2. 推送数据
    Map<String, Object> result = CommonUtil
        .o2m(opsappApiServiceI.update(new OpsappModel(opsappUpdateDataList, false)));
    TaskLogger.info(result.toString());
    if (result.isEmpty()) {
      TaskLogger.error("推送待办的接口异常，请排查原因！");
      return FAIL;
    } else if ("0.0".equalsIgnoreCase(result.get("errcode").toString())) {
      TaskLogger.info("推送待办成功！共{}条~", opsappUpdateDataList.size());
      return SUCCESS;
    } else {
      TaskLogger.error("推送待办发生错误！");
      return FAIL;
    }
  }
}

