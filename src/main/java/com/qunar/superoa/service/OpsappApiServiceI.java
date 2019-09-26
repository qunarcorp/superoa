package com.qunar.superoa.service;

import com.qunar.superoa.model.OpsappModel;
import com.qunar.superoa.model.OpsappSendMessageInfo;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/10/23_下午9:21
 * @Despriction: opsapp相关接口
 */

@Service
@FeignClient(url = "${api.opsapp}", name = "opsapp.qunar.com")
public interface OpsappApiServiceI {

  /**
   * 发送Qtalk通知
   * @param opsappSendMessageInfo
   * @return
   */
  @RequestMapping(value = "/ops/opsapp/api/send_qtalk_message", method = RequestMethod.POST, produces = "application/json")
  Object sendQtalkMessage(@RequestBody OpsappSendMessageInfo opsappSendMessageInfo);

  /**
   * 推送待审批数据
   */
  @RequestMapping(value = "/ops/opsapp/api/update", method = RequestMethod.POST, produces = "application/json")
  Object update(@RequestBody OpsappModel opsappModel);

  /**
   * 增量推送待审批数据
   */
  @RequestMapping(value = "/ops/opsapp/api/update_patch", method = RequestMethod.POST, produces = "application/json")
  Object update_patch(@RequestBody OpsappModel opsappModel);

  /**
   * 推送已审批数据
   */
  @RequestMapping(value = "/ops/opsapp/api/push/history", method = RequestMethod.POST, produces = "application/json")
  Object history(@RequestBody Map opsappModel);
}
