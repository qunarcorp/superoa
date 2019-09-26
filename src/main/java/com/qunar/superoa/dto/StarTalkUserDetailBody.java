package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_上午11:36
 * @Despriction: 获取Qtalk信息的请求参数
 */

@Data
@Slf4j
public class StarTalkUserDetailBody {

    @ApiModelProperty("appcode")
    private String system = "ops_superoa";

    @ApiModelProperty("UserList")
    private List<Users> users;

    public StarTalkUserDetailBody(){

    }

    public StarTalkUserDetailBody(String userName) {
        this.system = "ops_superoa";
        List<Users> users = new ArrayList<>();
        users.add(new Users(userName));
        this.users = users;
        log.info("${} 正在获取StarTalkUserInfo信息", users);
    }
}

@Data
class Users {

    @ApiModelProperty("userId,用户唯一标识")
    private String username;

    @ApiModelProperty("用户域")
    private String host;

    Users(String userName) {
        this.username = userName;
        this.host = "qtalk.test.org";
    }
}