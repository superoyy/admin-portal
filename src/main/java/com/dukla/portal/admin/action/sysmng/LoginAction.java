package com.dukla.portal.admin.action.sysmng;

import com.dukla.base.domain.SysLogin;
import com.dukla.base.util.Kit;
import com.dukla.web.base.CoreConstant;
import com.dukla.web.base.GenericAction;
import com.dukla.web.base.RequestCheck;
import com.dukla.portal.admin.handler.UserHandler;
import com.dukla.web.pojo.ModuleTreeVo;
import com.dukla.web.pojo.UserModuleTreeVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dukla on 11/9/16.
 */
@Controller
@RequestMapping("/framework/login.do")
public class LoginAction extends GenericAction {

    @Autowired
    UserHandler userHandler;

    /*
     * 登录
     */
    @RequestMapping(method = RequestMethod.POST,params="action=login")
    public String login(HttpServletRequest request) throws Exception{
        String loginName=request.getParameter("loginName");
        request.setAttribute("loginName", loginName);
        String pwd=request.getParameter("pwd");

        boolean success=true;
        SysLogin sysLogin=null;
        UserModuleTreeVo userModuleTreeVo=null;
        //用户
        if(loginName==null || loginName.length()==0){
            request.setAttribute("loginMsg", "请输入登录账号");
            success=false;
        }else{
            List<SysLogin> sysLoginList=this.hibernateHandler.getEntityListByProperty(SysLogin.class, "loginName", loginName);
            if(sysLoginList.size()!=0){
                sysLogin=sysLoginList.get(0);
            }else{
                request.setAttribute("loginMsg", "登录账号不存在");
                success=false;
            }
        }
        //密码
        if(success){
            if(pwd==null || pwd.length()==0){
                request.setAttribute("loginMsg", "请输入密码");
                success=false;
            }else if(!Kit.getMD5Str(pwd).equals(sysLogin.getLoginPwd())){
                request.setAttribute("loginMsg", "密码错误");
                success=false;
            }
        }
        //启用/禁用
        if(success){
            if(!"0".equals(sysLogin.getLoginState())){
                request.setAttribute("loginMsg", "用户被禁用");
                success=false;
            }
        }
        //功能树
        if(success){
            //获取用户功能树
            userModuleTreeVo=this.userHandler.getUserModuleTree(sysLogin);
            if(userModuleTreeVo.getModuleTreeVo()==null || userModuleTreeVo.getModuleTreeVo().getSubModuleTreeVoList().size()==0){
                request.setAttribute("loginMsg", "未分配功能权限");
                success=false;
            }
        }
        if(success){
            request.getSession().setAttribute(CoreConstant.LOGIN, userModuleTreeVo);
            request.getSession().setAttribute("moduleTreeJson", this.genModuleTreeJson(userModuleTreeVo.getModuleTreeVo()));
            return "redirect:/";
        }else{
            return "/framework/login";
        }
    }

    /**
     * 产生模块树的json串
     */
    private JSONObject genModuleTreeJson(ModuleTreeVo moduleTreeVo){
        JSONObject json=new JSONObject();
        this.genJSON(moduleTreeVo, json);
        return json;
    }



    /**
     * 递归产生
     */
    private void genJSON(ModuleTreeVo moduleTreeVo,JSONObject treeJSON){
        JSONObject module=new JSONObject();
        if(moduleTreeVo.getSysModule() != null){
            module=JSONObject.fromObject(moduleTreeVo.getSysModule());
        }
        treeJSON.put("module",module);
        JSONArray subJSONS=new JSONArray();
        for(ModuleTreeVo subTree:moduleTreeVo.getSubModuleTreeVoList()){
            JSONObject subJson=new JSONObject();
            genJSON(subTree, subJson);
            subJSONS.add(subJson);
        }
        treeJSON.put("subModules",subJSONS);
    }


    /*
     * 注销
     */
    @RequestMapping(params="action=logout")
    public String logout(HttpSession session) throws Exception{
        session.invalidate();
        return "redirect:/";
    }

    /*
     * 修改密码
     */
    @RequestMapping(params="action=changePwd")
    public void changePwd(@RequestParam(value = "oldPwd",required=true) String oldPwd,
                          @RequestParam(value = "newPwd",required=true) String newPwd,
                          HttpServletResponse response,
                          HttpSession session) throws Exception{
        response.setContentType("text/json;charset=UTF-8");
        UserModuleTreeVo userVo=(UserModuleTreeVo) session.getAttribute(CoreConstant.LOGIN);
        JSONObject json=new JSONObject();
        if(userVo == null){
            json.put("passed",false);
            json.put("msg","会话超时,请重新登录");
            response.getWriter().write(json.toString());
            return;
        }
        if(!Kit.getMD5Str(oldPwd).equals(userVo.getSysLogin().getLoginPwd())){
            json.put("passed",false);
            json.put("msg","旧密码不匹配");
            response.getWriter().write(json.toString());
            return;
        }
        this.sqlHandler.execute("update sys_login set login_pwd=? where id=?",new Object[]{Kit.getMD5Str(newPwd),userVo.getSysLogin().getId()});
        json.put("passed",true);
        json.put("msg","修改成功,下次登录生效。");
        response.getWriter().write(json.toString());
    }

    /**
     * 取得模块树
     */
    @RequestCheck(checkSession = true)
    @RequestMapping(params="action=getChildrenModule")
    public void getChildrenModule(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String id=request.getParameter("id");
        UserModuleTreeVo userModuleTreeVo=(UserModuleTreeVo)request.getSession().getAttribute(CoreConstant.LOGIN);
        ModuleTreeVo rootModule=userModuleTreeVo.getModuleTreeVo();
        List<ModuleTreeVo> subModules=getSubModules(id,rootModule);
        JSONArray json=new JSONArray();
        for(ModuleTreeVo module:subModules){
            JSONObject node=new JSONObject();
            node.put("id", module.getSysModule().getId());
            node.put("text", module.getSysModule().getModuleName());
            JSONObject attributes=new JSONObject();
            attributes.put("urlValue", module.getSysModule().getUrl());
            node.put("attributes", attributes);
            if(module.getSubModuleTreeVoList()!=null && module.getSubModuleTreeVoList().size()!=0){
                node.put("children", new JSONArray());
                node.put("state", "closed");
            }else{
                node.put("iconCls", "icon-grid");
            }
            json.add(node);
        }
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(json.toString());

    }

    private List<ModuleTreeVo> getSubModules(String id,ModuleTreeVo moduleTreeVo){
        List<ModuleTreeVo> subModules=new ArrayList<>();
        if(id==null || id.length()==0){
            subModules=moduleTreeVo.getSubModuleTreeVoList();
        }else if(moduleTreeVo.getSubModuleTreeVoList() !=null && moduleTreeVo.getSubModuleTreeVoList().size() != 0){
            for(ModuleTreeVo subModuleTreeVo:moduleTreeVo.getSubModuleTreeVoList()){
                if(subModuleTreeVo.getSysModule().getId().equals(id)){
                    subModules=subModuleTreeVo.getSubModuleTreeVoList();
                }else{
                    subModules=getSubModules(id,subModuleTreeVo);
                }
                if(subModules.size()!=0){
                    break;
                }
            }
        }
        return subModules;
    }
}
