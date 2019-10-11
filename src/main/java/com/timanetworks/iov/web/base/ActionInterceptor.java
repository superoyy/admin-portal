package com.timanetworks.iov.web.base;

import com.timanetworks.iov.web.pojo.UserModuleTreeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求拦截器
 * 用于拦截所用对action请求，进行session和权限检查
 */
public class ActionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger= LoggerFactory.getLogger(ActionInterceptor.class);


    /**
     * 拦截所有对action的请求
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 不是继承自GenericAction,不进行检查
        HandlerMethod handlerMethod = (HandlerMethod) handler;
		if (handlerMethod==null || !(handlerMethod.getBean() instanceof GenericAction)) {
			return true;
		}
        RequestCheck requestCheck=handlerMethod.getMethodAnnotation(RequestCheck.class);
        //设置了检查注解
        if(requestCheck!=null){
            String ajax=request.getHeader("X-Requested-With");
            //session过期
            if(requestCheck.checkSession()){
                if(!this.hasSession(request)){
//                    if(ajax!=null && ajax.length()!=0){//ajax请求
//                        response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
//                        response.setContentType("text/plain;charset=UTF-8");
//                        response.getWriter().write("no session");
//                    }else{//普通网页请求
//                        response.sendRedirect("/framework/timeout");
//                    }
                    logger.info("check no session -> URI:{}?{}",request.getRequestURI(),request.getQueryString());
                    return false;
                }
            }
            //没有权限
            if(requestCheck.checkPermit()){
                if(!this.hasPermit(request)){
//                    if(ajax!=null && ajax.length()!=0){//ajax请求
//                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//                        response.setContentType("text/plain;charset=UTF-8");
//                        response.getWriter().write("no permission");
//                    }else{//普通网页请求
//                        response.sendRedirect("/framework/denied");
//                    }
                    UserModuleTreeVo userModuleTreeVo=(UserModuleTreeVo) request.getSession().getAttribute(CoreConstant.LOGIN);
                    logger.info("check no permission -> LOGIN_USER:{},URI:{}?{}",userModuleTreeVo.getSysLogin().getLoginName(),request.getRequestURI(),request.getQueryString());
                    return false;
                }
            }
        }
        return true;
	}

    //检查session
    private boolean hasSession(HttpServletRequest request){
        return request.getSession().getAttribute(CoreConstant.LOGIN) != null;
    }

    /**
     * 检查用户权限
     * 请求必须带上:_urlKey 参数
     * @param request
     * @return
     */
    private boolean hasPermit(HttpServletRequest request){
        if(!this.hasSession(request)){
            return false;
        }
        String url=request.getRequestURI()+"?"+request.getQueryString();
        UserModuleTreeVo userModuleTreeVo=(UserModuleTreeVo) request.getSession().getAttribute(CoreConstant.LOGIN);
        return userModuleTreeVo.hasPermit(url);
    }

}
