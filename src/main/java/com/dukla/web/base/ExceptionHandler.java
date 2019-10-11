package com.dukla.web.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/*
 * 异常拦截器
 */
public class ExceptionHandler implements HandlerExceptionResolver {

    private static final Logger logger= LoggerFactory.getLogger(ExceptionHandler.class);

    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception exception){

        logger.error(exception.getMessage(),exception);
        StringWriter w=new StringWriter();
        PrintWriter s=new PrintWriter(w);
        exception.printStackTrace(s);
        String errStr=w.toString();
        //后台打印错误
        logger.warn("WEB ERROR MESSAGE:{}", errStr);
        request.setAttribute("errMsg", errStr);
        return new ModelAndView("framework/error");
    }

}
