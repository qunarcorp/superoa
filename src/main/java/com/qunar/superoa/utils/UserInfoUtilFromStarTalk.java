package com.qunar.superoa.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.service.UserInfoApiServiceI;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction: 从starTalk获取员工详细汇报线信息
 * @Date:Created in 2:52 PM 2019/5/10
 * @Modify by:
 */
@Component
@Slf4j
public class UserInfoUtilFromStarTalk {

  @Autowired
  private UserInfoApiServiceI userInfoApiServiceI;

  /**
   * 获取员工汇报线信息
   * @param userId 员工id
   * @return 员工信息map
   */
  public Map<String, Object> getUserInfo(String userId) {
    //请求获取员工汇报线信息
    Map<String, String> userInfoBody = Maps.newHashMap();
    userInfoBody.put("userId", userId);
    String userInfoJson;
    try {
      userInfoJson = userInfoApiServiceI.getUserInfoByUserId(userInfoBody);
    } catch (Exception e) {
      log.error("获取员工汇报线信息接口失败", e);
      throw new FlowException(ResultEnum.FLOW_USER_INFO_FAIL);
    }
    Map<String, Object> userInfoJsonMap = CommonUtil.o2m(userInfoJson);
    //校验是否获取成功
    if (!"true".equalsIgnoreCase(String.valueOf(userInfoJsonMap.get("ret")))) {
      String errMsg = String.valueOf(userInfoJsonMap.get("errmsg"));
      log.error("获取员工汇报线信息失败:" + errMsg + "---员工id" + userId);
      throw new FlowException(ResultEnum.FLOW_USER_INFO_ERROR);
    }
    //返回员工汇报线详细信息
    return CommonUtil.o2m(userInfoJsonMap.get("data"));
  }

  /**
   * 获取员工直属tl
   * @param userId 员工id
   * @return 员工直属tl
   */
  public String getUserTL(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userTL;
    try {
      userTL = String.valueOf(userInfoMap.get("tl"));
    } catch (Exception e) {
      log.error("获取员工直属TL为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_LEADER_IS_NULL);
    }
    if (StringUtils.isBlank(userTL) || "null".equalsIgnoreCase(userTL)) {
      log.error("获取员工直属TL为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_LEADER_IS_NULL);
    }
    return userTL;
  }

  /**
   * 获取员工部门总监
   * @param userId 员工id
   * @return 员工部门总监
   */
  public String getUserDeptLeader(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userDepLeader;
    try {
      userDepLeader = String.valueOf(userInfoMap.get("depLeader"));
    } catch (Exception e) {
      log.error("获取员工部门总监为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_DIRECTOR_ERROR);
    }
    if (StringUtils.isBlank(userDepLeader) || "null".equalsIgnoreCase(userDepLeader)) {
      log.error("获取员工部门总监为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_DIRECTOR_ERROR);
    }
    return userDepLeader;
  }

  /**
   * 获取员工部门分管领导
   * @param userId 员工id
   * @return 员工部门分管领导
   */
  public String getUserDepVP(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userDepVP;
    try {
      userDepVP = String.valueOf(userInfoMap.get("depLeader"));
    } catch (Exception e) {
      log.error("获取员工部门分管领导为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_VP_ERROR);
    }
    if (StringUtils.isBlank(userDepVP) || "null".equalsIgnoreCase(userDepVP)) {
      log.error("获取员工部门分管领导为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_VP_ERROR);
    }
    return userDepVP;
  }

  /**
   * 获取员工hrbp
   * @param userId 员工id
   * @return 员工hrbp
   */
  public String getUserHrbp(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userHrbp;
    try {
      userHrbp = String.valueOf(userInfoMap.get("hrbp"));
    } catch (Exception e) {
      log.error("获取员工hrbp为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_HRBP_IS_NULL);
    }
    if (StringUtils.isBlank(userHrbp) || "null".equalsIgnoreCase(userHrbp)) {
      log.error("获取员工hrbp为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_HRBP_IS_NULL);
    }
    return userHrbp;
  }

  /**
   * 获取员工hrd 人力资源部门总监
   * @param userId 员工id
   * @return 员工hrd
   */
  public String getUserHrd(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userHrd;
    try {
      userHrd = String.valueOf(userInfoMap.get("hrd"));
    } catch (Exception e) {
      log.error("获取员工hrbp为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_HRD_ERROR);
    }
    if (StringUtils.isBlank(userHrd) || "null".equalsIgnoreCase(userHrd)) {
      log.error("获取员工hrbp为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_HRD_ERROR);
    }
    return userHrd;
  }

  /**
   * 获取员工部门级别列表
   * @param userId 员工id
   * @return 员工部门级别列表
   */
  public List<String> getUserAllDeptList(String userId) {
    Map<String, Object> userInfoMap = getUserInfo(userId);
    String userDepartment;
    try {
      userDepartment = String.valueOf(userInfoMap.get("department"));
    } catch (Exception e) {
      log.error("获取员工部门为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_DEPT_IS_NULL);
    }
    if (StringUtils.isBlank(userDepartment) || "null".equalsIgnoreCase(userDepartment)) {
      log.error("获取员工部门为空,员工id: {}", userId);
      throw new FlowException(ResultEnum.FLOW_DEPT_IS_NULL);
    }
    List<String> userDeptList = Lists.newArrayList();
    String[] deptArray = userDepartment.split("/");
    for (int i = 0; i < deptArray.length; i++) {
      if (StringUtils.isNotBlank(deptArray[i]) && !"null".equalsIgnoreCase(deptArray[i])) {
        userDeptList.add(deptArray[i]);
      }
    }
    return userDeptList;
  }

  /**
   * 获取员工指定级别的部门
   * @param userId 员工id
   * @param level 部门级别
   * @return 员工指定级别的部门
   */
  public String getUserDeptByLevel(String userId, int level) {
    List<String> userDeptList = getUserAllDeptList(userId);
    if (level > userDeptList.size()) {
      log.error("获取员工指定级别部门失败，员工id: {}, 部门级别: {}", userId, level);
      throw new FlowException(ResultEnum.FLOW_DEPT_ERROR);
    }
    return userDeptList.get(level - 1);
  }

  /**
   * 获取员工一级部门和二级部门字符串
   * @param userId 员工id
   * @return 员工一级部门和二级部门
   */
  public String getUserDept(String userId) {
    StringBuilder dept = new StringBuilder();
    List<String> userDeptList = getUserAllDeptList(userId);
    if (userDeptList.size() <= 0) {
      dept.append("暂无部门信息");
    } else if (userDeptList.size() == 1) {
      dept.append(userDeptList.get(0));
    } else {
      dept.append(userDeptList.get(0)).append("/").append(userDeptList.get(1));
    }
    return dept.toString();
  }

  /**
   * 获取员工全部部门字符串
   * @param userId 员工id
   * @return 员工全部部门
   */
  public String getUserAllDept(String userId) {
    StringBuilder dept = new StringBuilder();
    List<String> userDeptList = getUserAllDeptList(userId);
    if (userDeptList.size() <= 0) {
      dept.append("暂无部门信息");
    } else {
      for (int i = 0; i < userDeptList.size(); i++) {
        dept.append(userDeptList.get(i));
        if (i < userDeptList.size() - 1) {
          dept.append("/");
        }
      }
    }
    return dept.toString();
  }

}
