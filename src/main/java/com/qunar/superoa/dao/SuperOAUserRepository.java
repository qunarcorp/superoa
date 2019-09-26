package com.qunar.superoa.dao;

import com.qunar.superoa.model.SuperOAUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 11:11 AM 2019/5/21
 * @Modify by:
 */
public interface SuperOAUserRepository extends JpaRepository<SuperOAUser, String> {

  /**
   * 根据部门id获取全部用户
   */
  List<SuperOAUser> findAllByDeptId(String deptId);

  /**
   * 根据部门id获取全部用户id
   */
  @Query(value = "select userName from SuperOAUser where deptId = ?1 order by userName asc")
  List<String> findAllUserNameByDeptId(String deptId);

  /**
   * 根据userName获取用户信息
   */
  List<SuperOAUser> findByUserName(String userName);

}
