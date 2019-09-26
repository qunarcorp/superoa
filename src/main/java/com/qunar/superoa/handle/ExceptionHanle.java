package com.qunar.superoa.handle;

import com.qunar.superoa.dto.Result;
import com.qunar.superoa.exceptions.*;
import com.qunar.superoa.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHanle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        if(e instanceof GirlException){
            GirlException girlException = (GirlException) e;
            return ResultUtil.error(girlException.getCode(), girlException.getMessage());
        }
        if (e instanceof RoleException) {
            RoleException roleException = (RoleException)e;
            return ResultUtil.error(roleException.getCode(), roleException.getMessage());
        }
        if (e instanceof UploadException) {
            UploadException uploadException = (UploadException)e;
            return ResultUtil.error(uploadException.getCode(), uploadException.getMessage());
        }
        if (e instanceof AgentException) {
            AgentException agentException = (AgentException)e;
            return ResultUtil.error(agentException.getCode(), agentException.getMessage());
        }
        if (e instanceof InvokeException) {
            InvokeException invokeException = (InvokeException)e;
            return ResultUtil.error(invokeException.getCode(), invokeException.getMessage());
        }
        if (e instanceof SuperOALoginException) {
            SuperOALoginException superOALoginException = (SuperOALoginException)e;
            return ResultUtil.error(superOALoginException.getCode(), superOALoginException.getMessage());
        }
        if (e instanceof FlowException) {
            FlowException flowException = (FlowException)e;
            return ResultUtil.error(flowException.getCode(), flowException.getMessage());
        }
        if (e instanceof FlowModelException) {
            FlowModelException flowModelException = (FlowModelException)e;
            return ResultUtil.error(flowModelException.getCode(), flowModelException.getMessage());
        }
        if (e instanceof ActivitiFlowException) {
            ActivitiFlowException activitiFlowException = (ActivitiFlowException)e;
            return ResultUtil.error(activitiFlowException.getCode(), activitiFlowException.getMessage());
        }
        logger.error("【系统异常】: {}", e.getMessage(), e);
        return ResultUtil.error(-1, e.getMessage());
    }
}
