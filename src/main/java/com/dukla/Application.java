package com.dukla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * Created by dukla on 6/22/16.
 */
@SpringBootApplication
@ServletComponentScan
public class Application {
    public static void main(String[] args){
        ConfigurableApplicationContext applicationContext=SpringApplication.run(Application.class, args);
        applicationContext.registerShutdownHook();
    }
}
