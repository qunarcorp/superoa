package com.qunar.superoa.utils;

import com.google.common.collect.Lists;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.DepartmentRepository;
import com.qunar.superoa.dao.SuperOAUserRepository;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowException;
import com.qunar.superoa.model.Department;
import com.qunar.superoa.model.SuperOAUser;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction: 获取员工详细汇报线信息
 * @Date:Created in 10:23 AM 2019/5/29
 * @Modify by:
 */
@Component
@Slf4j
public class UserInfoUtil {

  @Autowired
  private SuperOAUserRepository superOAUserRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  /**
   * 获取员工直属tl
   * @param userId 员工id
   * @return 员工直属tl
   */
  public String getUserTL(String userId) {
    try {
      SuperOAUser superOAUser = superOAUserRepository.findByUserName(userId).get(0);
      return superOAUser.getLeader();
    } catch (Exception e) {
      log.error("获取员工直属tl失败", e);
      throw new RuntimeException("获取员工直属TL信息失败");
    }
  }

  /**
   * 获取员工部门相关领导，包括部门总监deptDirector,部门分管领导VP,人力资源总监hrd
   * @param userId 员工id
   * @return 员工部门相关领导信息
   */
  public String getUserDeptLeader(String userId, String type) {
    try {
      SuperOAUser superOAUser = superOAUserRepository.findByUserName(userId).get(0);
      Department department = departmentRepository.findById(superOAUser.getDeptId()).get();
      AtomicReference<String> deptLeader = new AtomicReference<>();
      if (department.getDeep() <= 1) {
        if (Constant.DEPT_DIRECTOR.equalsIgnoreCase(type)) {
          deptLeader.set(department.getLeaders());
        } else if (Constant.DEPT_VP.equalsIgnoreCase(type)) {
          deptLeader.set(department.getVp());
        } else if (Constant.DEPT_HRD.equalsIgnoreCase(type)) {
          deptLeader.set(department.getHrbps());
        }
      } else {
        Arrays.stream(department.getParentStr().split("\\$")).forEach(pid -> {
          if (StringUtils.isNotBlank(pid) && departmentRepository.findById(pid).get().getDeep() == 1) {
            if (Constant.DEPT_DIRECTOR.equalsIgnoreCase(type)) {
              deptLeader.set(departmentRepository.findById(pid).get().getLeaders());
            } else if (Constant.DEPT_VP.equalsIgnoreCase(type)) {
              deptLeader.set(departmentRepository.findById(pid).get().getVp());
            } else if (Constant.DEPT_HRD.equalsIgnoreCase(type)) {
              deptLeader.set(departmentRepository.findById(pid).get().getHrbps());
            }
          }
        });
      }
      return deptLeader.get();
    } catch (Exception e) {
      log.error("获取员工部门"+ type + "失败", e);
      throw new RuntimeException("获取员工部门" + type +"信息失败");
    }
  }

  /**
   * 获取员工hrbp
   * @param userId 员工id
   * @return 员工hrbp
   */
  public String getUserHrbp(String userId) {
    try {
      SuperOAUser superOAUser = superOAUserRepository.findByUserName(userId).get(0);
      String hrbp = null;
      // 获取员工自身hrbp
      if (StringUtils.isNotBlank(superOAUser.getHr())) {
        hrbp = superOAUser.getHr();
        //获取员工所在部门hrbp
      } else {
        Department department = departmentRepository.findById(superOAUser.getDeptId()).get();
        if (StringUtils.isNotBlank(department.getHrbps())) {
          hrbp = department.getHrbps();
          // 获取员工上级部门hrbp
        } else {
          String pid = department.getPid();
          while (departmentRepository.findById(pid).isPresent()) {
            if (StringUtils.isNotBlank(departmentRepository.findById(pid).get().getHrbps())) {
              hrbp = departmentRepository.findById(pid).get().getHrbps();
              break;
            }
            pid = departmentRepository.findById(pid).get().getPid();
          }
        }
      }
      if (StringUtils.isNotBlank(hrbp)) {
        return hrbp;
      } else {
        throw new RuntimeException("获取员工hrbp信息失败");
      }
    } catch (Exception e) {
      log.error("获取员工hrbp失败", e);
      throw new RuntimeException("获取员工hrbp信息失败");
    }
  }

  /**
   * 获取员工部门级别列表
   * @param userId 员工id
   * @return 员工部门级别列表
   */
  public List<String> getUserAllDeptList(String userId) {
    try {
      SuperOAUser superOAUser = superOAUserRepository.findByUserName(userId).get(0);
      String userDept = superOAUser.getDeptStr();
      List<String> userDeptList = Lists.newArrayList();
      String[] deptArray = userDept.split("/");
      for (int i = 0; i < deptArray.length; i++) {
        if (StringUtils.isNotBlank(deptArray[i]) && !"null".equalsIgnoreCase(deptArray[i])) {
          userDeptList.add(deptArray[i]);
        }
      }
      return userDeptList;
    } catch (Exception e) {
      log.error("获取员工部门级别列表失败", e);
      throw new RuntimeException("获取员工部门级别列表失败");
    }
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
      dept.append("无部门信息");
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
    try {
      SuperOAUser superOAUser = superOAUserRepository.findByUserName(userId).get(0);
       return superOAUser.getDeptStr();
    } catch (Exception e) {
      log.error("获取员工部门失败", e);
      throw new RuntimeException("获取员工部门失败");
    }
  }


}
