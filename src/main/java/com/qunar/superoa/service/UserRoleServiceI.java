package com.qunar.superoa.service;

import com.qunar.superoa.dto.RoleDto;
import com.qunar.superoa.dto.UserRoleDto;
import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.model.UserRole;

import java.util.List;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 18:00 2018/8/30
 */
public interface UserRoleServiceI {
    List<UserRole> getRoleByUser(SuperOAUser superOAUser);

    List<RoleDto> getRoleByUserName (String userName);

    List<UserRoleDto> getAllRoleUser ();

    List<UserRole> getRoleByUserNameAndRoleType (String userName, String roleType);

    List<RoleDto> getRolesWithCurrentUser ();

    boolean addRole (String userName, List<String> roles);

    boolean modifyRole (UserRole userRole);

    boolean removeRole (UserRole userRole);

    boolean removeRoleByUserNameAndRoleType(String userName, String roleType);

    boolean removeRolesByUserName (String userName);

}
