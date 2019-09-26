package com.qunar.superoa.controller;

import com.qunar.superoa.dto.Result;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ActivitiServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/16_下午8:23
 * @Despriction: 工作流控制
 */
@Api(value = "activitiController", tags = "流程控制")
@RestController
@RequestMapping("activiti")
public class ActivitiController {

    private final static Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Autowired
    private ActivitiServiceI activitiServiceI;

    /**
     * 发起流程
     * @param key
     */
    @ApiOperation("发起流程")
    @PostMapping(value = "start")
    public Result<Task> startProcessInstance(@ApiParam(name = "key" ,value = "流程key", required = true) @RequestParam("key") String key,
                                             @ApiParam(name = "keys" ,value = "内容keys", required = false) @RequestParam("keys") String keys,
                                             @ApiParam(name = "values" ,value = "内容values", required = false) @RequestParam("values") String values) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.startProcessInstance(key, keys, values, SecurityUtils.currentUsername()));
    }

    /**
     * 根据流程部署id查询流程实例列表
     * @param deploymentId
     */
    @ApiOperation("查询流程实例")
    @PostMapping(value = "getFlow")
    public Result<List<Map<String, Object>>> getFlowByDeploymentId(@ApiParam(name = "deploymentId" ,value = "流程部署Id", required = true) @RequestParam("deploymentId") String deploymentId) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.getFlowByDeploymentId(deploymentId));
    }

    /**
     * 根据taskId同意流程
     * @param taskId
     */
    @ApiOperation("同意流程")
    @PostMapping(value = "complete")
    public Result<String> completeByTaskId(@ApiParam(name = "taskId" ,value = "taskId", required = true) @RequestParam("taskId") String taskId) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.completeByTaskId(taskId));
    }

    /**
     * 根据taskId拒绝流程
     * @param taskId
     */
    @ApiOperation("拒绝流程")
    @PostMapping(value = "rejectTask")
    public Result<String> rejectTask(@ApiParam(name = "taskId" ,value = "taskId", required = true) @RequestParam("taskId") String taskId,
                             @ApiParam(name = "reason" ,value = "拒绝原因", required = true) @RequestParam("reason") String reason) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.rejectTask(taskId, reason));
    }

    /**
     * 根据taskId退回流程
     * @param taskId
     */
    @ApiOperation("退回流程")
    @PostMapping(value = "backTask")
    public Result<String> backTask(@ApiParam(name = "taskId" ,value = "taskId", required = true) @RequestParam("taskId") String taskId,
                             @ApiParam(name = "reason" ,value = "退回原因", required = true) @RequestParam("reason") String reason) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.backTask(taskId, reason));
    }

    /**
     * 查询指定人下的所有流程
     * @param userId
     */
    @ApiOperation("查询指定人下的所有流程任务")
    @PostMapping(value = "getTasks")
    public Result<List<Map<String, Object>>> getTasksByUserId(@ApiParam(name = "userId" ,value = "userId", required = true) @RequestParam("userId") String userId) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.getTasksByUserId(userId));
    }

    /**
     * 查询指定人下的所有流程
     */
    @ApiOperation("获取当前登录用户的流程任务数")
    @GetMapping(value = "countTask")
    public Result<Integer> getcountTask() throws Exception{
        return ResultUtil.run(() -> activitiServiceI.getcountTask());
    }

    /**
     * test
     * @param taskId
     */
    @ApiOperation("test")
    @PostMapping(value = "test")
    public Result<String> test(@ApiParam(name = "taskId" ,value = "taskId", required = true) @RequestParam("taskId") String taskId) throws Exception{
        activitiServiceI.test(taskId);
        return ResultUtil.run(() -> "ok");
    }

    /**
     * 获取指定流程实例的当前节点审批人
     * @param taskId
     */
    @ApiOperation("获取指定流程实例的当前节点审批人")
    @PostMapping(value = "getApproveUser")
    public Result<String> getApproveUser(@ApiParam(name = "taskId" ,value = "taskId", required = true) @RequestParam("taskId") String taskId) throws Exception{
        return ResultUtil.run(() -> activitiServiceI.getApproveUser(taskId));
    }
}
