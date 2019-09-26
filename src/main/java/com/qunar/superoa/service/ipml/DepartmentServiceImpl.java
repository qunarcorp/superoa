package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.DepartmentRepository;
import com.qunar.superoa.dao.SuperOAUserRepository;
import com.qunar.superoa.dto.DepartmentDto;
import com.qunar.superoa.model.Department;
import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.service.DepartmentServiceI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午3:04 2018/10/29
 * @Modify by:
 */
@Service
public class DepartmentServiceImpl implements DepartmentServiceI {

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private SuperOAUserRepository superOAUserRepository;

  /**
   * 模糊查询部门整体路径含有kw名称的所有部门
   */
  @Override
  public List<Department> getDeptByK(String kw) {
    return departmentRepository.findAllByNodeNameStrContaining(kw);
  }

  /**
   * 获取整个部门树
   */
  @Override
  public Object getDeptTrees() {
    List<Object> deptTrees = Lists.newArrayList();
    // 获取公司根节点
    List<Department> deptLv1List = departmentRepository.findAllByDeep(0);
    for (Department dept1 : deptLv1List) {
      getDeptNodes(dept1, deptTrees);
    }
    return deptTrees;
  }

  /**
   * 根据部门递归获取部门树节点map
   *
   * @param dept 部门department
   * @param list 树节点列表
   */
  private void getDeptNodes(Department dept, List<Object> list) {
    Map<String, Object> treeNodeMap = Maps.newHashMap();
    treeNodeMap.put("name", dept.getName());
    treeNodeMap.put("id", dept.getId());
    List<String> usersName = superOAUserRepository.findAllUserNameByDeptId(dept.getId());
    if (usersName.size() > 0) {
      treeNodeMap.put("users", usersName);
    }
    //添加部门下所有用户信息
    Map<String, Object> deptAllUsers = Maps.newHashMap();
    deptAllUsers.put("usersName", getDeptNodeAllUsers(dept));
    deptAllUsers.put("usersSize", getDeptNodeAllUsers(dept).size());
    treeNodeMap.put("allUsers", deptAllUsers);
    List<Department> deptChildren = departmentRepository.findAllByPid(dept.getId());
    // 递归添加子节点
    if (deptChildren.size() > 0) {
      List<Object> childrenList = Lists.newArrayList();
      for (Department department : deptChildren) {
        getDeptNodes(department, childrenList);
      }
      treeNodeMap.put("children", childrenList);
    }
    list.add(treeNodeMap);
  }

  /**
   * 获取该部门下所有用户数
   */
  private Set getDeptNodeAllUsers(Department department) {
    Set<String> users = Sets.newHashSet();
    List<SuperOAUser> deptUsers;
    if (department.getDeep() > 0) {
      deptUsers = superOAUserRepository.findAllByDeptId(department.getId());
      List<Department> childRenDepts = departmentRepository.findAllByParentStrContaining(department.getId());
      for (Department dept : childRenDepts) {
        List<SuperOAUser> tempUserList = superOAUserRepository.findAllByDeptId(dept.getId());
        deptUsers.addAll(tempUserList);
      }
    } else {
      deptUsers = superOAUserRepository.findAll();
    }
    deptUsers.forEach(oaUser -> users.add(oaUser.getUserName()));
    return users;
  }

  /**
   * 获取部门详细信息
   */
  @Override
  public Object getDeptInfo(String id) {
    if (!departmentRepository.findById(id).isPresent()) {
      throw new RuntimeException("获取部门信息失败,数据库不存在的id");
    }
    Department department = departmentRepository.findById(id).get();
    DepartmentDto departmentDto = new DepartmentDto();
    departmentDto.setDeep(department.getDeep());
    departmentDto.setHrbps(department.getHrbps());
    departmentDto.setId(department.getId());
    departmentDto.setLeaders(department.getLeaders());
    departmentDto.setName(department.getName());
    departmentDto.setPid(department.getPid());
    departmentDto.setVp(department.getVp());
    departmentDto.setNodeNameStr(department.getNodeNameStr());

    return departmentDto;
  }

  /**
   * 添加新部门 - 包括部门名称、部门vp、部门leaders、部门hrbps、部门层级、上级部门id
   */
  @Override
  public Department addDepartment(DepartmentDto departmentDto) {
    Department department = new Department();
    department = getDepartmentBasic(department, departmentDto);
    if (departmentRepository.findByName(departmentDto.getName()).size() > 0) {
      throw new RuntimeException("部门名称不能重复");
    }
    if (StringUtils.isBlank(departmentDto.getPid())) {
      throw new RuntimeException("父部门id不能为空");
    }
    Department parDept = departmentRepository.findById(departmentDto.getPid()).get();
    department.setDeep(parDept.getDeep() + 1);

    department.setPid(departmentDto.getPid());
    //设置nodeNameStr 和 parentStr
    if (department.getDeep() == 1) {
      department.setNodeNameStr(departmentDto.getName());
      department.setParentStr("");
    } else {
      //获取倒序父部门id列表
      List<String> pidListDesc = Lists.newArrayList();
      String pid = departmentDto.getPid();
      for (int i = 1; i < departmentDto.getDeep(); i++) {
        pidListDesc.add(pid);
        if (departmentRepository.findById(pid).isPresent()) {
          pid = departmentRepository.findById(pid).get().getPid();
        }
      }
      // 获取父部门id列表 - 从一级到n级
      List<String> pidList = Lists.reverse(pidListDesc);
      //根据父部门id列表获取nodeNameStr和parentStr
      StringBuilder parentStr = new StringBuilder();
      StringBuilder nodeNameStr = new StringBuilder();
      for(String parentId : pidList) {
        parentStr.append("$").append(parentId);
        nodeNameStr.append(departmentRepository.findById(parentId).get().getName()).append("/");
      }
      nodeNameStr.append(departmentDto.getName());
      department.setParentStr(parentStr.toString());
      department.setNodeNameStr(nodeNameStr.toString());
    }
    return departmentRepository.save(department);
  }

  /**
   * 修改部门信息 - 部门名称、部门VP、部门leaders、部门hrbps
   */
  @Override
  public Department updateDepartment(DepartmentDto departmentDto) {
    Department department = departmentRepository.findById(departmentDto.getId()).get();
    String oldDeptName = department.getName();
    department = getDepartmentBasic(department, departmentDto);
    //处理由于部门名称改变而导致的各相关部门nodeNameStr改变,以及员工部门的改变
    if (!oldDeptName.equals(department.getName())) {
      List<Department> departmentList = departmentRepository.findAllByParentStrContaining(departmentDto.getId());
      departmentList.forEach(dept -> {
        String newNodeNameStr = dept.getNodeNameStr().replaceAll(oldDeptName, departmentDto.getName());
        dept.setNodeNameStr(newNodeNameStr);
        departmentRepository.save(dept);
        List<SuperOAUser> users = superOAUserRepository.findAllByDeptId(dept.getId());
        users.forEach(oaUser -> oaUser.setDeptStr(newNodeNameStr));
        superOAUserRepository.saveAll(users);
      });
      String newNodeName = department.getNodeNameStr().replaceAll(oldDeptName, departmentDto.getName());
      department.setNodeNameStr(newNodeName);
      List<SuperOAUser> oaUsers = superOAUserRepository.findAllByDeptId(department.getId());
      oaUsers.forEach(oaUser -> oaUser.setDeptStr(newNodeName));
      superOAUserRepository.saveAll(oaUsers);
    }
    return departmentRepository.save(department);
  }

  /**
   * 根据departmentDto设置department基础信息 - 名称、hrbps、leaders、vp
   */
  private Department getDepartmentBasic(Department department, DepartmentDto departmentDto) {
    String name = departmentDto.getName();
    if (StringUtils.isBlank(name)) {
      throw new RuntimeException("部门名称不能为空");
    }
    department.setName(name);
    String hrbps = departmentDto.getHrbps().replaceAll("\\[", Constant.FILL_BLANK)
        .replaceAll("]", Constant.FILL_BLANK);
    if (StringUtils.isBlank(hrbps) && departmentDto.getDeep() == 1) {
      throw new RuntimeException("一级部门hrbps不能为空");
    }
    department.setHrbps(hrbps);
    String leaders = departmentDto.getLeaders().replaceAll("\\[", Constant.FILL_BLANK)
        .replaceAll("]", Constant.FILL_BLANK);
    if (StringUtils.isBlank(leaders) && departmentDto.getDeep() == 1) {
      throw new RuntimeException("一级部门leaders不能为空");
    }
    department.setLeaders(leaders);
    department.setVp(departmentDto.getVp());
    return department;
  }

  /**
   * 根据部门id删除部门
   */
  @Override
  public String deleteDepartment(String id) {
    if (departmentRepository.findAllByPid(id).size() > 0) {
      throw new RuntimeException("该部门有子部门，无法删除");
    }
    if (superOAUserRepository.findAllByDeptId(id).size() > 0) {
      throw new RuntimeException("该部门下有员工存在，无法删除");
    }
    departmentRepository.deleteById(id);
    return "删除成功";
  }
}
