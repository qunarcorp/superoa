package com.qunar.superoa.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Auther: chengyan.liang
 * @Despriction: editor路径转到modeler.html
 * @Date:Created in 11:42 AM 2019/3/26
 * @Modify by:
 */
@Api(value = "activitiDesignPageController", tags = "editor路径转到modeler.html模版")
@Controller
public class ActivitiDesignPageController {
  @GetMapping("/editor")
  public String toModelerHtml(){
    return "/modeler";
  }
}
