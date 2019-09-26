package com.qunar.superoa.service;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 下午3:00 2018/10/29
 * @Modify by:
 */

import com.qunar.superoa.dto.DepartmentDto;
import com.qunar.superoa.model.Department;

/**
 * 部门信息操作
 */
public interface DepartmentServiceI {

  /**
   * 模糊查询部门信息
   */
  Object getDeptByK(String kw);

  /**
   * 获取整个部门树
   */
  Object getDeptTrees();

  /**
   * 获取部门详细信息
   */
  Object getDeptInfo(String id);

  /**
   * 修改部门信息 - 部门名称、部门VP、部门leaders、部门hrbps
   */
  Department updateDepartment(DepartmentDto departmentDto);

  /**
   * 添加新部门 - 包括部门名称、部门vp、部门leaders、部门hrbps、部门层级、上级部门id
   */
  Department addDepartment(DepartmentDto departmentDto);

  /**
   * 删除部门
   */
  String deleteDepartment(String id);

}
