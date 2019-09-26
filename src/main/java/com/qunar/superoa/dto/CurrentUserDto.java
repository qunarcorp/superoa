package com.qunar.superoa.dto;

import com.google.gson.internal.LinkedTreeMap;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.SuperOALoginException;
import com.qunar.superoa.utils.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午2:49
 * @Despriction: 当前登录的用户
 */

@Data
public class CurrentUserDto {

  @ApiModelProperty("Qtalk账户名")
  private String qtalk;

  @ApiModelProperty("头像")
  private String avatar;

  @ApiModelProperty("中文名")
  private String cname;

  @ApiModelProperty("当前用户权限")
  private List<LinkedTreeMap> currentAuthority;

  @ApiModelProperty("性别")
  private String gender;

  @ApiModelProperty("心情")
  private String mood;

  public CurrentUserDto(Object currentUserInfo) throws SuperOALoginException {
    try {
      Map<String, Object> stringObjectMap = CommonUtil.o2m(currentUserInfo);
      this.qtalk = (String) stringObjectMap.get("userName");
      this.avatar = (String) stringObjectMap.get("avatar");
      this.cname = (String) stringObjectMap.get("cname");
      this.currentAuthority = (List<LinkedTreeMap>) stringObjectMap.get("roles");
      this.mood = (String) stringObjectMap.get("mood");
      this.gender = "1".equalsIgnoreCase(String.valueOf(stringObjectMap.get("gender"))) ? "男" : "女";
    } catch (Exception e) {
      throw new SuperOALoginException(ResultEnum.GET_CURRENT_USER_ERROR);
    }
  }
}
