package com.qunar.superoa.utils;

import com.google.common.collect.Lists;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.AttachmentRepository;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.UploadException;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.security.SecurityUtils;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.DeleteError;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction: minio对象存储
 * @Date:Created in 3:31 PM 2019/4/23
 * @Modify by:
 */
@Slf4j
@Component
public class MinioUtils {

  private static ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

  @Autowired
  private AttachmentRepository attachmentRepository;

  @Autowired
  private FileMD5Utils fileMD5Utils;

  @Autowired
  private InputStreamToOutUtils inputStreamToOutUtils;

  @Value("${minio.url}")
  private String minioUrl;

  @Value("${minio.accessKey}")
  private String accessKey;

  @Value("${minio.secretKey}")
  private String secretKey;

  @Value("${minio.bucketName}")
  private String bucketName;

  /**
   * 初始化MinioClient
   */
  private MinioClient getMinioClient() throws Exception {
    MinioClient minioClient = null;
    try {
      minioClient = new MinioClient(minioUrl, accessKey, secretKey);
    } catch (Exception e) {
      log.error("初始化MinioClient失败", e);
      throw new Exception("初始化MinioClient失败", e);
    }
    boolean found = false;
    try {
      found = minioClient.bucketExists(bucketName);
    } catch (Exception e) {
      log.error("查询bucket失败", e);
      throw new Exception("查询bucket失败", e);
    }
    if (!found) {
      try {
        minioClient.makeBucket(bucketName);
      } catch (Exception e) {
        log.error("初始化bucket失败", e);
        throw new Exception("初始化bucket失败", e);
      }
    }
    return minioClient;
  }

  /**
   * 上传activiti流程图关联图片到服务器并返回URL
   * @return 存好的userAttachment对象
   */
  public UserAttachment uploadImageToMinio(InputStream inputStream, String relationId) throws Exception {
    String currentUser = SecurityUtils.currentUsername();
    if ("anonymousUser".equals(currentUser)) {
      throw new UploadException(ResultEnum.UPLOAD_ERROR);
    }
    //获取图片名称
    List<String> list = processEngine.getRepositoryService()
        .getDeploymentResourceNames(relationId);
    String fileName = relationId;
    if (list != null && list.size() > 0) {
      for (String name : list) {
        if (name.contains(".png") && !name.contains("_")) {
          fileName = name;
        }
      }
      fileName = ((fileName.contains(".png") && !fileName.contains("_")) ? fileName
          : fileName + ".svg");
    } else {
      //上传二维码
      fileName = currentUser + "_" + DateTimeUtil.getDate("_") + "_" + DateTimeUtil.getTime() + "_" + relationId + "_" + "QRCode" + ".png";
    }
    //创建几个相同的流
    ByteArrayOutputStream outputStream = inputStreamToOutUtils.inputStreamToOut(inputStream);
    InputStream inputStreamMd5 = new ByteArrayInputStream(outputStream.toByteArray());
    InputStream inputStreamMinio = new ByteArrayInputStream(outputStream.toByteArray());
    InputStream inputStreamSize = new ByteArrayInputStream(outputStream.toByteArray());
    String url = minioUrl + "/" + bucketName + "/" + fileName;
    UserAttachment userAttachment = new UserAttachment();
    //校验文件md5值
    String md5 = fileMD5Utils.getMd5ByInputStream(inputStreamMd5);
    Optional<UserAttachment> optionalUserAttachment = attachmentRepository
        .findFirstByMd5OrderByUploadDateDesc(md5);
    //设置文件mime值，默认为二进制流
    String fileContentType = "application/octet-stream";
    if (fileName.contains(".svg")) {
      fileContentType = "image/svg+xml";
    } else if (fileName.contains(".png")) {
      fileContentType = "image/png";
    }
    if (optionalUserAttachment.isPresent()) {
      userAttachment.setUrl(optionalUserAttachment.get().getUrl());
    } else {
      getMinioClient().putObject(bucketName, fileName, inputStreamMinio, (long) inputStreamSize.available(), fileContentType);
      userAttachment.setUrl(url);
    }
    userAttachment.setAttachName(fileName);
    userAttachment.setMd5(md5);
    userAttachment.setExist(false);
    userAttachment.setUsername(currentUser);
    userAttachment.setExtension(".PNG");
    userAttachment.setUploadDate(new Date());
    userAttachment.setSize((long) inputStreamSize.available());
    userAttachment.setRelationId(relationId);
    return attachmentRepository.save(userAttachment);
  }

  /**
   * 将流程跟踪图上传到服务器并设置过期时间 - 1小时
   */
  public UserAttachment uploadTraceImageForUrl(InputStream inputStream, String fileName) throws Exception {
    String currentUser = SecurityUtils.currentUsername();
    if ("anonymousUser".equals(currentUser)) {
      throw new UploadException(ResultEnum.UPLOAD_ERROR);
    }
    String uploadFileName = currentUser + "_" + DateTimeUtil.getDate("_") + "_" + DateTimeUtil.getTime() + "_" + fileName + "当前审批节点流程图.svg";
    String url = minioUrl + "/" + bucketName + "/" + uploadFileName;
    //创建几个相同的流
    ByteArrayOutputStream outputStream = inputStreamToOutUtils.inputStreamToOut(inputStream);
    InputStream inputStreamMd5 = new ByteArrayInputStream(outputStream.toByteArray());
    InputStream inputStreamMinio = new ByteArrayInputStream(outputStream.toByteArray());
    InputStream inputStreamSize = new ByteArrayInputStream(outputStream.toByteArray());
    UserAttachment userAttachment = new UserAttachment();
    //校验文件md5值
    String md5 = fileMD5Utils.getMd5ByInputStream(inputStreamMd5);
    Optional<UserAttachment> optionalUserAttachment = attachmentRepository
        .findFirstByMd5OrderByUploadDateDesc(md5);
    if (optionalUserAttachment.isPresent()) {
      userAttachment.setUrl(optionalUserAttachment.get().getUrl());
    } else {
      getMinioClient().putObject(bucketName, uploadFileName, inputStreamMinio, (long) inputStreamSize.available(), "image/svg+xml");
      userAttachment.setUrl(url);
    }
    //设置过期时间
    userAttachment.setExpiredDate(new DateTime().plusHours(1).toDate());
    userAttachment.setAttachName(uploadFileName);
    userAttachment.setExist(false);
    userAttachment.setExtension(".svg");
    userAttachment.setMd5(md5);
    userAttachment.setUploadDate(new Date());
    userAttachment.setUsername(currentUser);
    userAttachment.setSize((long) inputStreamSize.available());
    return attachmentRepository.save(userAttachment);
  }

  /**
   * 上传文件到服务器并返回URL
   *
   * @return 存好的userAttachment对象
   */
  public UserAttachment uploadFileToMinio(MultipartFile file) throws Exception {
    String currentUser = SecurityUtils.currentUsername();
    if ("anonymousUser".equals(currentUser)) {
      throw new UploadException(ResultEnum.UPLOAD_ERROR);
    }
    File f = null;
    try {
      f = File.createTempFile("tmp", null);
      file.transferTo(f);
      f.deleteOnExit();
    } catch (IOException e) {
      e.printStackTrace();
      log.error("转换multipart文件失败",e);
    }
    //避免上传文件名不合规范
    String fixedFileName = Objects.requireNonNull(file.getOriginalFilename())
        .replaceAll("\\\\", Constant.FILL_BLANK)
        .replaceAll("（", Constant.FILL_BLANK)
        .replaceAll("）", Constant.FILL_BLANK)
        .replaceAll(Constant.FILL_SPACE, Constant.FILL_BLANK);
    String uploadFileName = currentUser + "_" + DateTimeUtil.getDate("_") + "_" + DateTimeUtil.getTime() + "_" + fixedFileName;
    String url = minioUrl + "/" + bucketName + "/" + uploadFileName;
    InputStream inputStreamMinio = new FileInputStream(f);
    UserAttachment userAttachment = new UserAttachment();
    //校验文件md5值
    Optional<UserAttachment> optionalUserAttachment = attachmentRepository
        .findFirstByMd5OrderByUploadDateDesc(fileMD5Utils.getMd5ByFile(f));
    //获取文件的MIME值
    String fileContentType = "application/octet-stream";
    try {
      fileContentType = file.getContentType();
    } catch (Exception e) {
      log.error("获取文件MIME值失败", e);
    }
    if (optionalUserAttachment.isPresent()) {
      userAttachment.setUrl(optionalUserAttachment.get().getUrl());
    } else {
      getMinioClient().putObject(bucketName, uploadFileName, inputStreamMinio, (long) inputStreamMinio.available(), fileContentType);
      userAttachment.setUrl(url);
    }
    userAttachment.setMd5(fileMD5Utils.getMd5ByFile(f));
    userAttachment.setAttachName(uploadFileName);
    userAttachment.setExist(false);
    userAttachment.setUsername(currentUser);
    userAttachment.setExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
    userAttachment.setUploadDate(new Date());
    userAttachment.setSize(file.getSize());
    userAttachment.setRelationId("");
    return attachmentRepository.save(userAttachment);
  }

  /**
   * 从minio容器删除单个文件
   * @param fileName 文件名称
   * @return 删除结果
   */
  public Boolean deleteFileFromMinio(String fileName) {
    //执行删除操作
    try {
      getMinioClient().removeObject(bucketName, fileName);
      return true;
    } catch (Exception e) {
      log.error("从minio删除文件失败",e);
      log.error("删除失败文件名称: {}",fileName);
      return false;
    }
  }

  /**
   * 从minio容器删除文件列表
   * @param fileNameList 文件名称列表
   * @return 删除失败的文件名称
   */
  public List<String> deleteFileFromMinio(List<String> fileNameList) throws Exception{
    Iterable<Result<DeleteError>> results = getMinioClient().removeObjects(bucketName, fileNameList);
    List<String> errorFileNameList = Lists.newArrayList();
    results.forEach(error -> {
      try {
        errorFileNameList.add(error.get().objectName());
      } catch (Exception e) {
        log.error("获取minio删除文件失败信息失败",e);
      }
    });
    return errorFileNameList;
  }

}
