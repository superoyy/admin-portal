package com.dukla.portal.admin;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * Created by dukla on 10/12/19.
 */
@Configuration
public class ApplicationConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //设置文件大小限制
        factory.setMaxFileSize("1024KB"); //KB,MB
        //设置总上传数据总大小
        factory.setMaxRequestSize("1024KB");
        return factory.createMultipartConfig();
    }

}
