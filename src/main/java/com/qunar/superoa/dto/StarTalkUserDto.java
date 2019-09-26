package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_上午10:51
 * @Despriction:
 */

@Data
public class StarTalkUserDto {

    @ApiModelProperty("Qtalk账号")
    private String qtalk;

    @ApiModelProperty("中文名")
    private String cname;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("心情")
    private String mood;

    public StarTalkUserDto(ArrayList<Map<String, String>> listData) {
        listData.forEach(stringStringMap -> {
            qtalk = stringStringMap.get("user_id");
            cname = stringStringMap.get("user_name");
            avatar = stringStringMap.get("url");
            gender = stringStringMap.get("gender");
            mood = stringStringMap.get("mood");
        });
    }
}
