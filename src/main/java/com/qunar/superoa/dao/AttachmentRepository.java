package com.qunar.superoa.dao;

import com.qunar.superoa.model.UserAttachment;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/7_下午7:23
 * @Despriction: 附件表
 */

public interface AttachmentRepository extends JpaRepository<UserAttachment, String> {


  UserAttachment findByRelationId(String relation);

  Optional<List<UserAttachment>> findAllByExpiredDateBefore(Date date);

  /**
   *根据md5查找附件内容
   */
  Optional<UserAttachment> findFirstByMd5OrderByUploadDateDesc(String md5);

  /**
   * 根据文件名查找附件内容
   */
  Optional<UserAttachment> findByAttachName(String fileName);

}
