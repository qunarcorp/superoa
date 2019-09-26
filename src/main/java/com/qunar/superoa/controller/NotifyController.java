package com.qunar.superoa.controller;

import com.qunar.superoa.dto.NotifyDto;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.dto.PageAble;
import com.qunar.superoa.service.NotifyServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/12_下午2:39
 * @Despriction: NotifyController
 */

@Api(value = "通知", tags =  "通知")
@RestController
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private NotifyServiceI notifyServiceI;


    @PostMapping("/findByCurrentUser")
    public Result<PageResult<NotifyDto>> findByCurrentUser(@RequestBody PageAble pageAble){
        return ResultUtil.success(notifyServiceI.findCurrentUserNotify(pageAble));
    }

    @PostMapping("/findAll")
    public Result<PageResult<NotifyDto>> findAll(@RequestBody PageAble pageAble){
        return ResultUtil.success(notifyServiceI.findAll(pageAble));
    }


    @PostMapping("/findUnread")
    public Result<PageResult<NotifyDto>> findUnread(@RequestBody PageAble pageAble){
        return ResultUtil.success(notifyServiceI.findUnread(pageAble));
    }

    @PostMapping("/read")
    public Result read(@ApiParam("ids (多个用逗号分隔)") @RequestParam String ids){
        notifyServiceI.readNotify(ids);
        return ResultUtil.success();
    }
}
