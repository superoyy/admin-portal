package com.dukla.portal.admin.web.action;

import com.dukla.web.base.CoreConstant;
import com.dukla.web.base.GenericAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by dukla on 11/15/16.
 */
@Controller
public class IndexAction extends GenericAction {

    private boolean isLogin(HttpSession session){
        return session.getAttribute(CoreConstant.LOGIN) != null;
    }

    @RequestMapping(method = RequestMethod.GET,path = "/")
    public String toMain(HttpSession session){
        return isLogin(session) ? "/framework/main" : "/framework/login";
    }

//    @RequestMapping(method = RequestMethod.GET,path = "/error")
//    public String toError(){
//        return "/framework/error";
//    }


    @RequestMapping(method = RequestMethod.GET,path = "/framework/toLogin")
    public String toLogin(){
        return "/framework/login";
    }

    @RequestMapping(method = RequestMethod.GET,path = "/iov/toIndex")
    public String toIovIndex(){
        return "/iov/index_stat";
    }


}
