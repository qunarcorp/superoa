package com.qunar.superoa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction: http请求返回的最外层对象
 * @Modify by:
 */

@Data
public class Result<T> {

    /** 错误码 */
    @ApiModelProperty(value = "错误码", name = "错误码")
    private Integer status;

    /** 提示信息 */
    @ApiModelProperty(value = "错误码描述", name = "返回消息，成功为“success”，失败为具体失败信息")
    private String message;

    /** 具体的内容 */
    @ApiModelProperty(value = "数据对象", name = "数据对象")
    private T data;
}
