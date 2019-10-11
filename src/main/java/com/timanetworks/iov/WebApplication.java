package com.timanetworks.iov;

import com.timanetworks.iov.core.jpa.handler.HibernateHandler;
import com.timanetworks.iov.domain.SysCodes;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.CoreConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by dukla on 6/22/16.
 */
@EnableEurekaClient
@SpringBootApplication
@ServletComponentScan
public class WebApplication {

    private static final Logger logger= LoggerFactory.getLogger(WebApplication.class);


    public static void main(String[] args){
        ConfigurableApplicationContext applicationContext=SpringApplication.run(WebApplication.class, args);
        HibernateHandler hibernateHandler=applicationContext.getBean(HibernateHandler.class);
        //加载系统参数
        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("codeOrder", "asc");
        List<SysCodes> sysCodesList=hibernateHandler.getEntityListByProperty(SysCodes.class, "codeType", "SYS_PROP", orderProps);
        for(SysCodes sysCodes:sysCodesList){
            CoreConstant.SYS_PROP.put(sysCodes.getCodeKey(), sysCodes.getCodeValue());
            logger.info("Load CoreConstant {}->{}", sysCodes.getCodeKey(), sysCodes.getCodeValue());
        }
        logger.info("系统启动,"+ Kit.formatDateTime(new Date(), "yyyy.MM.dd HH:mm:ss"));

        applicationContext.registerShutdownHook();
    }

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
