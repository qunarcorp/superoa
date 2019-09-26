package com.qunar.superoa.config;

import com.qunar.superoa.interceptor.WebSocketInterceptor;
import com.qunar.superoa.service.WebSocketServer;
import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: yang.du
 * @Description: WebSocket的配置类
 * @Date: Created in 14:52 2018/9/4
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer(), "/websocket").addInterceptors(new WebSocketInterceptor());
    }

//    @Bean
    public WebSocketServer  webSocketServer() {
        return new WebSocketServer ();
    }

}
