package com.dukla.portal.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * Created by dukla on 6/22/16.
 */
@SpringBootApplication
@ComponentScan("com.dukla")
@ServletComponentScan
public class Application {
    public static void main(String[] args){
        ConfigurableApplicationContext applicationContext=SpringApplication.run(Application.class, args);
        applicationContext.registerShutdownHook();
    }
}
