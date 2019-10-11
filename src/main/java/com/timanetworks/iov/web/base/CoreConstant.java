package com.timanetworks.iov.web.base;

import com.timanetworks.iov.util.Kit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebListener
public class CoreConstant implements ServletContextListener {

    private static final Logger logger= LoggerFactory.getLogger(CoreConstant.class);

	//系统启动时生成
    public static Map<String,String> SYS_PROP=new HashMap<>();
	
	public static String LOGIN="_login";
	
	public static String CONTEXT_ROOT="_context_root";
	
	public static String CONTEXT_REAL_PATH="_context_real_path";


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext())
                .getAutowireCapableBeanFactory().autowireBean(this);
        CoreConstant.SYS_PROP.put(CoreConstant.CONTEXT_ROOT, servletContextEvent.getServletContext().getContextPath());
        logger.info("Load CoreConstant {}->{}",CoreConstant.CONTEXT_ROOT,servletContextEvent.getServletContext().getContextPath());
        CoreConstant.SYS_PROP.put(CoreConstant.CONTEXT_REAL_PATH, servletContextEvent.getServletContext().getRealPath("/"));
        logger.info("Load CoreConstant {}->{}",CoreConstant.CONTEXT_REAL_PATH,servletContextEvent.getServletContext().getRealPath("/"));
        servletContextEvent.getServletContext().setAttribute("SYS_CONSTANT",CoreConstant.SYS_PROP);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("系统关闭,"+Kit.formatDateTime(new Date(),"yyyy.MM.dd HH:mm:ss"));
    }


}
