package com.qunar.superoa.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/8/27_6:34 PM
 * @Despriction: 获取qtalk二维码
 */

@Data
public class StartQRCodeV2 {

    @ApiParam("qtalk 还是 qchat")
    private String p;

    @ApiParam("系统名称")
    private String type;
}
