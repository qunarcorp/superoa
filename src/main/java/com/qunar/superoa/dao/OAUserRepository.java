package com.qunar.superoa.dao;

import com.qunar.superoa.model.SuperOAUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author: chengyan.liang
 * @Description:
 * @Date: Created in 17:52 2018/8/29
 */
public interface OAUserRepository extends JpaRepository<SuperOAUser, String>, JpaSpecificationExecutor<SuperOAUser> {

    /**
     * 根据userId查询用户
     */
    List<SuperOAUser> findByUserName(String username);

    /**
     * 根据用户中文名查询所有用户
     */
    List<SuperOAUser> findAllByCname (String cname);

    /**
     * 根据kw模糊查询cname含有kw的所有用户
     */
    List<SuperOAUser> findAllByCnameContaining(String kw);

    /**
     * 根据kw模糊查询userName含有kw的所有用户
     */
    List<SuperOAUser> findAllByUserNameContaining(String kw);

    /**
     * 查询所有leader为userName的用户
     */
    List<SuperOAUser> findAllByLeader(String userName);

    /**
     * 查询所有hr为userName的用户
     */
    List<SuperOAUser> findAllByHr(String userName);

}
