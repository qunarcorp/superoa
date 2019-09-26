package com.qunar.superoa.controller;

import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.dto.Result;
import com.qunar.superoa.service.FormDataServiceI;
import com.qunar.superoa.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by xing.zhou on 2018/8/30.
 */
//@Api(value = "FromDataController", tags =  "表单数据相关")
//@RestController
//@RequestMapping("fromData")
public class FormDataController {

    private final static Logger logger = LoggerFactory.getLogger(FormDataController.class);


    @Autowired
    private FormDataServiceI formDataServiceI;


    @ApiOperation("获取表单数据")
    @PostMapping(value = "getFromDateById")
    public Result<FormDataDto> getFromDateById(@ApiParam(name = "procInstId", value = "流程id", required = true) @RequestParam("procInstId") String procInstId) throws Exception {
        return ResultUtil.run(() -> formDataServiceI.getFromDataByProcInstId(procInstId));
    }


//    @ApiOperation("新建流程表单数据")
//    @PostMapping(value = "addFromDate")
//    public Result<String> addFromDate(@RequestBody FormDataDto formDataDto) throws Exception {
//        return ResultUtil.run(() -> formDataServiceI.addFromDate(formDataDto));
//    }

}
