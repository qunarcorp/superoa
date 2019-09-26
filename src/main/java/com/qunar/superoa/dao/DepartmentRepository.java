package com.qunar.superoa.dao;

import com.qunar.superoa.model.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @Auther: chengyan.liang
 * @Descpriction:
 * @Date:Created in 下午3:29 2018/10/29
 * @Modify by:
 */
public interface DepartmentRepository extends JpaRepository<Department, String>{


  /**
   * 根据关键字获取全部部门信息
   * @param nodeNameStr k，如 - isdev
   */
  List<Department> findAllByNodeNameStrContaining(String nodeNameStr);

  /**
   * 根据关键字id获取全部部门信息
   * @param id 部门id
   */
  List<Department> findAllByParentStrContaining(String id);

  /**
   * 根据层级获取所有部门名称
   * @param deep 层级
   * @return
   */
  @Query(value = "select distinct name from Department where deep = ?1 order by name asc")
  List<String> findAllDeptNameByDeep(Integer deep);

  /**
   * 根据层级获取所有部门对象
   */
  List<Department> findAllByDeep(Integer deep);

  /**
   * 根据父id - pid获取所有部门对象
   */
  List<Department> findAllByPid(String pid);

  /**
   * 查询所有leaders包含userName的部门
   */
  List<Department> findAllByLeadersContaining(String userName);

  /**
   * 查询所有vp包含userName的部门
   */
  List<Department> findAllByVpContaining(String userName);

  /**
   * 查询所有hrbps包含userName的部门
   */
  List<Department> findAllByHrbpsContaining(String userName);

  /**
   * 根据name查询部门
   */
  List<Department> findByName(String name);

}
