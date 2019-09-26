package com.qunar.superoa.dao;

import com.qunar.superoa.dto.UserRoleDto;
import com.qunar.superoa.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 17:53 2018/8/29
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByRoleType (String roleType);

    List<UserRole> findByQtalk (String qtalk);

    List<UserRole> findByQtalkAndRoleType (String qtalk, String roleType);

    @Query(value = "SELECT  r.qtalk as username,string_agg (r.role_type,',') as role,string_agg (r.role_name,',') as roleName FROM user_role r GROUP BY qtalk", nativeQuery = true)
    List<Map<String, String>> findAllUser ();

    int deleteByQtalkAndRoleType (String qtalk, String roleType);

    int deleteByQtalk (String qtalk);
}
