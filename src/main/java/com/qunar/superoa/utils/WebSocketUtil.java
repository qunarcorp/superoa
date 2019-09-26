package com.qunar.superoa.utils;

import com.google.gson.Gson;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.service.WebSocketServer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

/**
 * @Author: yang.du
 * @Description:
 * @Date: Created in 9:54 2018/9/10
 */
@Component
public class WebSocketUtil {

    private static WebSocketServer webSocketServer = new WebSocketServer();

    /**
     * 向前端推送消息
     * @param target:用户Qtalk
     * @param o: 推送信息封装的对象
     * @return 是否成功
     */
    public static boolean pushMessage (String target, Object o) {
        Gson gosn = new Gson();
        return webSocketServer.sendMessageToUser(target, new TextMessage(gosn.toJson(ResultUtil.success(o))));
    }

    public static void pushMessage(Notify notify) {

    }
}
