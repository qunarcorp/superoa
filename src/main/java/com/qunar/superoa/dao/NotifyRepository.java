package com.qunar.superoa.dao;

import com.qunar.superoa.model.Notify;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_下午9:06
 * @Despriction:
 */

public interface NotifyRepository  extends JpaRepository<Notify, String> {

    List<Notify> findByQtalk(String qtalk);

    Page<Notify> findByQtalk(String qtalk, Pageable pageAble);

    List<Notify> findByIdIn(List<String> idList);

    Page<Notify> findAll(Specification<Notify> spec, Pageable pageAble);

    List<Notify> findAllByQtalkStatus(int qtalkStatus);

    List<Notify> findAllByMailStatus(int mailStatus);
 }
