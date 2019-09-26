package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction: Mobel http请求返回的最外层对象
 * @Modify by:
 */

@Data
public class MobelResult<T> {

    /** 错误码 */
    @ApiModelProperty(value = "错误码", name = "错误码")
    private Integer errcode;

    /** 提示信息 */
    @ApiModelProperty(value = "错误码描述", name = "返回消息，成功为“success”，失败为具体失败信息")
    private String msg;

    /** 具体的内容 */
    @ApiModelProperty(value = "数据对象", name = "数据对象")
    private T data;
}
