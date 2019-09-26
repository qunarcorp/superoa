package com.qunar.superoa.dao;

import com.qunar.superoa.model.FormData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xing.zhou on 2018/8/30.
 */
public interface FormDataRepository extends JpaRepository<FormData, String> {

    FormData findByProcInstId(String procInstId);
}
