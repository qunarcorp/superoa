package com.qunar.superoa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: lee.guo
 * @Despriction: 获取配置文件中的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "girl")
public class GirlProperties {
    private String cupSize;
    private Integer age;
    private String content;
}
