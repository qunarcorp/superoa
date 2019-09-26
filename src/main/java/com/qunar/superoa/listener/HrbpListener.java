package com.qunar.superoa.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

/**
 * 获取hrbp 为当前节点审批人
 * Created by xing.zhou on 2018/9/14.
 */
@Component
public class HrbpListener extends CandidatesListener {

    /**
     * 重写设置节点操作着方法
     *
     * @param userTask
     * @param delegateExecution
     */
    @Override
    public void setGroupApproveUser(UserTask userTask, DelegateExecution delegateExecution) {
        String owner = delegateExecution.getVariable("owner").toString();
        List<String> candidateUsers = new ArrayList<String>();
        String hrbps = userInfoUtil.getUserHrbp(owner);
        Arrays.stream(hrbps.replaceAll(";",",").split(",")).forEach(hrbp ->{
            if(!hrbp.isEmpty()){
                candidateUsers.add(hrbp);
            }
        });
        userTask.setCandidateUsers(candidateUsers);
    }
}
