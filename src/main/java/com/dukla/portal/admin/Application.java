package com.dukla.portal.admin;

import com.dukla.base.domain.SysCodes;
import com.dukla.base.jpa.handler.HibernateHandler;
import com.dukla.base.util.Kit;
import com.dukla.web.base.CoreConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
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
@SpringBootApplication
@ServletComponentScan
public class Application {

    private static final Logger logger= LoggerFactory.getLogger(Application.class);


    public static void main(String[] args){
        ConfigurableApplicationContext applicationContext=SpringApplication.run(Application.class, args);
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
