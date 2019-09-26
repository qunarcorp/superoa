package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.DepartmentRepository;
import com.qunar.superoa.dao.OAUserRepository;
import com.qunar.superoa.dto.OAUserDto;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.PasswordDto;
import com.qunar.superoa.dto.QueryUserDto;
import com.qunar.superoa.model.SuperOAUser;
import com.qunar.superoa.model.UserAttachment;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.OAUserServiceI;
import com.qunar.superoa.utils.DateTimeUtil;
import com.qunar.superoa.utils.MinioUtils;
import com.qunar.superoa.utils.RandomStringUtil;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 10:35 AM 2019/5/22
 * @Modify by:
 */
@Slf4j
@Service
public class OAUserServiceImpl implements OAUserServiceI {

  @Autowired
  private OAUserRepository oaUserRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private MinioUtils minioUtils;

  /**
   * 管理员获取所有用户信息
   */
  @Override
  public PageResult<OAUserDto> adminGetAllUserInfo(QueryUserDto queryUserDto) {
    // 系统管理员权限校验
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    PageResult<OAUserDto> pageResult = new PageResult<>();
    Page<SuperOAUser> page = oaUserRepository.findAll((Specification<SuperOAUser>)
        (root, query, cb) -> createOAUserPredicate(root, cb, queryUserDto), queryUserDto.getPageAble());
    List<OAUserDto> oaUserDtoList = Lists.newArrayList();
    if (page.getContent().size() > 0) {
      page.getContent().forEach(oaUser -> {
        // 创建
        OAUserDto oaUserDto = getOAUserDto(oaUser);
        // 添加
        oaUserDtoList.add(oaUserDto);
      });
      pageResult.setContent(oaUserDtoList);
      pageResult.setPageable(page.getPageable());
      pageResult.setTotal((int) page.getTotalElements());
    }
    return pageResult;
  }

  /**
   * 将SuperOAUser转换为传到前端的dto对象
   */
  private OAUserDto getOAUserDto(SuperOAUser oaUser) {
    OAUserDto oaUserDto = new OAUserDto();
    oaUserDto.setId(oaUser.getId());
    oaUserDto.setAvatar(oaUser.getAvatar());
    oaUserDto.setCname(oaUser.getCname());
    oaUserDto.setDeptId(oaUser.getDeptId());
    oaUserDto.setDeptStr(oaUser.getDeptStr());
    oaUserDto.setGender(oaUser.getGender());
    oaUserDto.setLeader(oaUser.getLeader());
    oaUserDto.setHr(oaUser.getHr());
    oaUserDto.setUserName(oaUser.getUserName());
    oaUserDto.setPhone(oaUser.getPhone());
    oaUserDto.setEmail(oaUser.getEmail());
    return oaUserDto;
  }

  @Override
  public List searchOAUser(String key) {
    List<SuperOAUser> oaUserListByUserName = oaUserRepository.findAllByUserNameContaining(key);
    List<SuperOAUser> oaUserListByCname = oaUserRepository.findAllByCnameContaining(key);
    Set<SuperOAUser> oaUserSet = Sets.newHashSet();
    oaUserSet.addAll(oaUserListByUserName);
    oaUserSet.addAll(oaUserListByCname);
    List<Map<String, String>> oaUserInfoList = Lists.newArrayList();
    oaUserSet.forEach(oaUser -> {
      Map<String, String> oaUserInfo = Maps.newHashMap();
      oaUserInfo.put("value", oaUser.getUserName());
      oaUserInfo.put("label", oaUser.getCname() + "(" + oaUser.getUserName() + ")" + Constant.FILL_SPACE + oaUser.getDeptStr());
      oaUserInfoList.add(oaUserInfo);
    });
    return oaUserInfoList;
  }

  /**
   * 创建OAUser查询过滤器
   */
  private Predicate createOAUserPredicate(Path root, CriteriaBuilder cb, QueryUserDto queryUserDto) {
    Predicate predicate = cb.conjunction();
    if (StringUtils.isNotBlank(queryUserDto.getKey())) {
      predicate.getExpressions().add(cb.or(
          cb.like(root.get("userName"), "%" + queryUserDto.getKey().trim() + "%"),
          cb.like(root.get("cname"), "%" + queryUserDto.getKey().trim() + "%"),
          cb.like(root.get("leader"), "%" + queryUserDto.getKey().trim() + "%"),
          cb.like(root.get("hr"), "%" + queryUserDto.getKey().trim() + "%"),
          cb.like(root.get("deptStr"), "%" + queryUserDto.getKey().trim() + "%")));
    }
    return predicate;
  }


  /**
   * 用户获取自身信息
   */
  @Override
  public Object getUserInfo() {
    String userName = SecurityUtils.currentUsername();
    if (oaUserRepository.findByUserName(userName).size() <= 0) {
      throw new RuntimeException("获取用户信息失败");
    }
    SuperOAUser superOAUser = oaUserRepository.findByUserName(userName).get(0);
    superOAUser.setPassword("");
    superOAUser.setSalt("");
    return superOAUser;
  }

  /**
   * 管理员根据id获取某个用户信息
   */
  @Override
  public Object adminGetUserInfo(String id) {
    // 系统管理员权限校验
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    if (!oaUserRepository.findById(id).isPresent()) {
      throw new RuntimeException("获取用户信息失败");
    }
    SuperOAUser superOAUser = oaUserRepository.findById(id).get();
    superOAUser.setPassword("");
    superOAUser.setSalt("");
    return superOAUser;
  }

  /**
   * 管理员添加用户
   */
  @Override
  public Object adminAddOAUser(OAUserDto oaUserDto) {
    checkUserInfo(oaUserDto);
    if (oaUserRepository.findByUserName(oaUserDto.getUserName()).size() > 0) {
      throw new RuntimeException("用户名不能重复");
    }
    // 添加用户
    SuperOAUser superOAUser = new SuperOAUser();
    superOAUser.setUserName(oaUserDto.getUserName());
    superOAUser.setCname(oaUserDto.getCname());
    superOAUser.setDeptId(oaUserDto.getDeptId());
    if (departmentRepository.findById(oaUserDto.getDeptId()).isPresent()) {
      superOAUser.setDeptStr(departmentRepository.findById(oaUserDto.getDeptId()).get().getNodeNameStr());
    } else {
      superOAUser.setDeptStr("无");
    }
    // 设置salt
    String salt = RandomStringUtil.getRandomCode(10, 6);
    superOAUser.setSalt(salt);
    // 设置密码
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String password = bCryptPasswordEncoder.encode(Constant.OA_USER_ORIGIN_PASSWORD + salt);
    superOAUser.setPassword(password);
    superOAUser.setPhone(oaUserDto.getPhone());
    superOAUser.setLeader(oaUserDto.getLeader());
    superOAUser.setHr(oaUserDto.getHr());
    superOAUser.setEmail(oaUserDto.getEmail());
    superOAUser.setUpdateTime(DateTimeUtil.getDateTime());
    superOAUser.setAvatar(Constant.OA_USER_AVATAR);
    superOAUser.setGender(oaUserDto.getGender());
    // 持久化
    return oaUserRepository.save(superOAUser);
  }

  /**
   * 管理员更新用户信息
   */
  @Override
  public Object adminUpdateOAUser(OAUserDto oaUserDto) {
    checkUserInfo(oaUserDto);
    if (!oaUserRepository.findById(oaUserDto.getId()).isPresent()) {
      throw new RuntimeException("用户不存在");
    }
    SuperOAUser superOAUser = oaUserRepository.findById(oaUserDto.getId()).get();
    // 更新用户信息,不能更新userName
    superOAUser.setCname(oaUserDto.getCname());
    superOAUser.setDeptId(oaUserDto.getDeptId());
    if (departmentRepository.findById(oaUserDto.getDeptId()).isPresent()) {
      superOAUser.setDeptStr(departmentRepository.findById(oaUserDto.getDeptId()).get().getNodeNameStr());
    } else {
      superOAUser.setDeptStr("无");
    }
    superOAUser.setPhone(oaUserDto.getPhone());
    superOAUser.setLeader(oaUserDto.getLeader());
    superOAUser.setHr(oaUserDto.getHr());
    superOAUser.setEmail(oaUserDto.getEmail());
    superOAUser.setUpdateTime(DateTimeUtil.getDateTime());
    superOAUser.setGender(oaUserDto.getGender());
    // 持久化
    return oaUserRepository.save(superOAUser);
  }

  /**
   * 管理员变更用户密码
   */
  @Override
  public Object adminUpdateOAUserPassword(PasswordDto passwordDto) {
    // 系统管理员权限校验
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    // 查询用户
    if (!oaUserRepository.findById(passwordDto.getId()).isPresent()) {
      throw new RuntimeException("用户不存在");
    }
    if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
      throw new RuntimeException("两次输入密码不一致，请重新输入");
    }
    SuperOAUser superOAUser = oaUserRepository.findById(passwordDto.getId()).get();
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    // 重新生成加密密码
    String newCryptPassword = bCryptPasswordEncoder.encode(passwordDto.getNewPassword() + superOAUser.getSalt());
    superOAUser.setPassword(newCryptPassword);
    oaUserRepository.save(superOAUser);
    return "修改密码成功";
  }

  /**
   * 系统管理员权限及用户信息校验
   */
  private void checkUserInfo(OAUserDto oaUserDto) {
    // 系统管理员权限校验
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    // 用户信息校验
    if (StringUtils.isBlank(oaUserDto.getUserName()) || StringUtils.isBlank(oaUserDto.getDeptId())
        || StringUtils.isBlank(oaUserDto.getPhone()) || StringUtils.isBlank(oaUserDto.getLeader())
        || StringUtils.isBlank(oaUserDto.getCname())) {
      throw new RuntimeException("用户名、中文名、所属部门、直属领导、手机号不能为空");
    }
  }

  /**
   * 管理员根据id删除用户
   */
  @Override
  public Object adminDeleteOAUserById(String id) {
    // 系统管理员权限校验
    if (!SecurityUtils.checkSysAdmin()) {
      throw new RuntimeException("无系统管理员权限");
    }
    if (!oaUserRepository.findById(id).isPresent()) {
      throw new RuntimeException("用户不存在");
    }
    SuperOAUser superOAUser = oaUserRepository.findById(id).get();
    if (oaUserRepository.findAllByLeader(superOAUser.getUserName()).size() > 0) {
      throw new RuntimeException("该用户为其他用户的leader,无法删除");
    }
    if (oaUserRepository.findAllByHr(superOAUser.getUserName()).size() > 0) {
      throw new RuntimeException("该用户为其他用户的hr,无法删除");
    }
    if (departmentRepository.findAllByLeadersContaining(superOAUser.getUserName()).size() > 0
        || departmentRepository.findAllByVpContaining(superOAUser.getUserName()).size() > 0
        || departmentRepository.findAllByHrbpsContaining(superOAUser.getUserName()).size() > 0) {
      throw new RuntimeException("该用户为部门的leaders或hrbps或vp,无法删除");
    }
    oaUserRepository.delete(superOAUser);
    return "删除用户成功";
  }

  /**
   * 用户更新自身信息
   */
  @Override
  public Object updateOAUserSelf(OAUserDto oaUserDto) {
    if (!oaUserRepository.findById(oaUserDto.getId()).isPresent()) {
      throw new RuntimeException("用户不存在");
    }
    SuperOAUser superOAUser = oaUserRepository.findById(oaUserDto.getId()).get();
    if (!Objects.equals(SecurityUtils.currentUsername(), superOAUser.getUserName())) {
      throw new RuntimeException("当前登录用户名和要修改用户名不同");
    }
    // 修改信息
    superOAUser.setPhone(oaUserDto.getPhone());
    superOAUser.setEmail(oaUserDto.getEmail());
    superOAUser.setMood(oaUserDto.getMood());
    superOAUser.setUpdateTime(DateTimeUtil.getDateTime());
    // 持久化
    return oaUserRepository.save(superOAUser);
  }

  /**
   * 用户更新密码
   */
  @Override
  public Object updatePassword(PasswordDto passwordDto) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    SuperOAUser superOAUser = oaUserRepository.findByUserName(SecurityUtils.currentUsername()).get(0);
    if (!bCryptPasswordEncoder.matches(passwordDto.getOldPassword() + superOAUser.getSalt(), superOAUser.getPassword())) {
      throw new RuntimeException("原密码输入错误");
    }
    if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
      throw new RuntimeException("两次输入密码不一致，请重新输入");
    }
    // 重新生成加密密码
    String newCryptPassword = bCryptPasswordEncoder.encode(passwordDto.getNewPassword() + superOAUser.getSalt());
    superOAUser.setPassword(newCryptPassword);
    oaUserRepository.save(superOAUser);
    return "修改密码成功";
  }

  /**
   * 用户更新头像
   */
  @Override
  public Object updateAvatar(MultipartFile imageFile) throws Exception {
    UserAttachment userAttachment = minioUtils.uploadFileToMinio(imageFile);
    SuperOAUser superOAUser = oaUserRepository.findByUserName(SecurityUtils.currentUsername()).get(0);
    if (!Constant.UPLOAD_AVATAR_EXTENSION.toLowerCase().contains(userAttachment.getExtension().toLowerCase())) {
      throw new RuntimeException("上传图片格式不符，须jpg、png、jpeg、gif");
    }
    superOAUser.setAvatar(userAttachment.getUrl());
    oaUserRepository.save(superOAUser);
    return "更新头像成功";
  }
}
