package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/7_下午7:25
 * @Despriction: 附件
 */

@Data
@Entity
@ApiModel("附件")
@Table(name = "user_attachment")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class UserAttachment {

  /**
   * ID
   */
  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  @ApiModelProperty("附件ID")
  private String id;


  /**
   * 关联Id
   */
  @ApiModelProperty("关联Id")
  private String relationId;

  /**
   * 附件名称
   */
  @ApiModelProperty("附件名称")
  private String attachName;

  /**
   * 上传人
   */
  @ApiModelProperty("上传人")
  private String username;

  /**
   * 附件URL
   */
  @ApiModelProperty("附件URL")
  private String url;

  /**
   * 文件md5值
   */
  @ApiModelProperty("文件md5值")
  private String md5;

  /**
   * 是否删除
   */
  @ApiModelProperty("是否删除")
  private Boolean exist;

  /**
   * 后缀名
   */
  @ApiModelProperty("后缀名")
  private String extension;

  /**
   * 文件大小
   */
  @ApiModelProperty("文件大小(kb)")
  private Long size;

  /**
   * 过期时间
   */
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
  @Temporal(TemporalType.TIMESTAMP)
  @ApiModelProperty("过期时间，为空则永不过期")
  private Date expiredDate;

  /**
   * 上传时间
   */
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
  @Temporal(TemporalType.TIMESTAMP)
  @ApiModelProperty("上传时间")
  private Date uploadDate;

  public String getUploadDate() {
    if (this.uploadDate == null) {
      return null;
    }
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.uploadDate);
  }

  public String getExpiredDate() {
    if (this.expiredDate == null) {
      return null;
    }
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.expiredDate);
  }
}
