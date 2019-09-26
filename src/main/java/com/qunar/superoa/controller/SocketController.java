package com.qunar.superoa.controller;

import com.qunar.superoa.dto.Result;
import com.qunar.superoa.dto.UserRoleDto;
import com.qunar.superoa.service.WebSocketServer;
import com.qunar.superoa.utils.ResultUtil;
import com.qunar.superoa.utils.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 16:59 2018/9/4
 */

@Controller
public class SocketController {
    private Logger logger = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/socket")
    public String socket () {
        return "online";
    }

    @GetMapping("/socket2")
    public String socket2 () {
        return "socket";
    }


    @PostMapping("/message")
    @ResponseBody
    public Result sendMessage(@RequestParam String clientId, @RequestParam String userName, @RequestParam String role) {
        UserRoleDto userRoleDto = new UserRoleDto(userName, role);
        return ResultUtil.success(WebSocketUtil.pushMessage(clientId, userRoleDto));
    }
}
