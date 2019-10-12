package com.dukla.portal.admin;

import com.dukla.base.domain.SysCodes;
import com.dukla.base.jpa.handler.HibernateHandler;
import com.dukla.base.util.Kit;
import com.dukla.web.base.CoreConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 10/12/19.
 */
@Component
public class ApplicationInit implements ApplicationListener<ApplicationReadyEvent> {

    static final Logger logger= LoggerFactory.getLogger(ApplicationInit.class);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(event.getApplicationContext()!=null){
            HibernateHandler hibernateHandler = event.getApplicationContext().getBean(HibernateHandler.class);
//            //加载系统参数
//            Map<String,String> orderProps=new HashMap<>();
//            orderProps.put("codeOrder", "asc");
//            List<SysCodes> sysCodesList=hibernateHandler.getEntityListByProperty(SysCodes.class, "codeType", "SYS_PROP", orderProps);
//            for(SysCodes sysCodes:sysCodesList){
//                CoreConstant.SYS_PROP.put(sysCodes.getCodeKey(), sysCodes.getCodeValue());
//                logger.info("Load CoreConstant {}->{}", sysCodes.getCodeKey(), sysCodes.getCodeValue());
//            }
            logger.info("系统启动,"+ Kit.formatDateTime(new Date(), "yyyy.MM.dd HH:mm:ss"));
        }

    }
}
