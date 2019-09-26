package com.qunar.superoa.utils;

import com.google.gson.Gson;
import com.qunar.superoa.controller.BaseController;
import com.qunar.superoa.dto.MobelResult;
import com.qunar.superoa.dto.Result;
import java.util.Map;
import org.springframework.validation.BindingResult;

/**
 * @Auther: lee.guo
 * @Despriction: result封装工具
 */
public class ResultUtil {

  /**
   * 返回成功
   */
  public static Result success() {
    return success(null);
  }

  /**
   * 返回成功 & Data
   */
  public static Result success(Object object) {
    Result result = new Result();
    result.setStatus(0);
    result.setMessage("成功");
    result.setData(object);
    return result;
  }

  /**
   * 移动端 返回成功
   */
  public static MobelResult mSuccess() {
    return mSuccess(null);
  }

  /**
   * 移动端 返回成功 & Data
   */
  public static MobelResult mSuccess(Object object) {
    MobelResult result = new MobelResult();
    result.setErrcode(0);
    result.setMsg("成功");
    result.setData(object);
    return result;
  }

  /**
   * 返回Gson成功 & Data
   */
  public static Result successForGson(Object object) {
    return success(new Gson().fromJson(new Gson().toJson(object), Object.class));
  }

  /**
   * 执行传递过来的类中的RUN方法
   */
  public static Result run(BaseController object) throws Exception {
    return success(object.run());
  }

  /**
   * 执行传递过来的类中的RUN方法 并根据校验信息返回成功或失败
   */
  public static Result validAndRun(BindingResult bindingResult, BaseController object)
      throws Exception {
    if (bindingResult.hasErrors()) {
      return error(-2, bindingResult.getFieldError().getDefaultMessage());
    }
    return success(object.run());
  }

  /**
   * 返回错误信息
   */
  public static Result error(Integer code, String msg) {
    Result result = new Result();
    result.setStatus(code);
    result.setMessage(msg);
    return result;
  }

  /**
   * 转为Map Result格式
   */
  public static Result O2MR(Object object) {
    Map<String, Object> map = CommonUtil.o2m(object);
    Result result = new Result();
    String errcode = map.get("errcode") + "";
    Integer code = errcode.indexOf(".") == -1 ? Integer.parseInt(errcode)
        : Integer.parseInt(errcode.substring(0, errcode.indexOf(".")));
    result.setStatus(code);
    result.setMessage(map.get("errmsg") + "");
    try {
      result.setData(CommonUtil.getMapData(object));
    } catch (Exception e) {
      result.setData(CommonUtil.getObjectData(object));
    }
    return result;
  }

  ;

  /**
   * 转为Array Result格式
   */
  public static Result O2AR(Object object) {
    Map<String, Object> map = CommonUtil.o2m(object);
    Result result = new Result();
    String errcode = map.get("errcode") + "";
    Integer code = Integer.parseInt(errcode.substring(0, errcode.indexOf(".")));
    result.setStatus(code);
    result.setMessage(map.get("errmsg") + "");
    result.setData(CommonUtil.getListData(object));
    return result;
  }

  ;
}
