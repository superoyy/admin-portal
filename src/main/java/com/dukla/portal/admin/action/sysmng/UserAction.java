package com.dukla.portal.admin.web.action.sysmng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.SysDept;
import com.timanetworks.iov.domain.SysLogin;
import com.timanetworks.iov.domain.SysLoginRole;
import com.timanetworks.iov.domain.SysRole;
import com.timanetworks.iov.util.Kit;
import com.dukla.web.base.CoreConstant;
import com.dukla.web.base.GenericAction;
import com.dukla.portal.admin.web.handler.UserHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 用户管理
 * @author 欧阳亚
 * @since 2012.12
 */
@Controller
@RequestMapping("/framework/sysmng/usermng.do")
public class UserAction extends GenericAction {

    @Autowired
    private UserHandler userHandler;
	 
	/*
	 * 入口
	 */
	@RequestMapping(params="action=entry")
	public String entry(Model model) throws Exception {
        model.addAttribute("title","用户列表");
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("roleOrder", "asc");
		List<SysRole> roles=this.hibernateHandler.getEntityListAll(SysRole.class, orderProps);
        model.addAttribute("roles", roles);
        return "/framework/sysmng/user_list";
	}
	 
	/*
	 * 列表
	 */
	@RequestMapping(params="action=list")
	public void list(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		
		String loginName=request.getParameter("loginName");
		String userName=request.getParameter("userName");
		String loginState=request.getParameter("loginState");
		
		List<QueryParam> queryParamList=new ArrayList<QueryParam>();
		if(loginName!=null && loginName.length()!=0){
			queryParamList.add(new QueryParam("and","loginName","like","%"+loginName+"%"));
		}
		if(userName!=null && userName.length()!=0){
			queryParamList.add(new QueryParam("and","userName","like","%"+userName+"%"));
		}
		if(loginState!=null && loginState.length()!=0 && !"-1".equals(loginState)){
			queryParamList.add(new QueryParam("and","loginState","=",loginState));
		}
		
		int total=this.hibernateHandler.getEntityCountByQueryParam(SysLogin.class, queryParamList);

        Page page=this.getPage(request.getParameter("page"),request.getParameter("rows"));

		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put(sort, order);
		
		List<SysLogin> list=this.hibernateHandler.getEntityListByQueryParam(SysLogin.class, queryParamList, page.getStart(), page.getCount(), orderProps);
	
		JSONArray rows=new JSONArray();
		orderProps.clear();
		orderProps.put("sysRole.roleOrder", "asc");
		for(SysLogin sysLogin:list){
			JSONObject jsonRow=JSONObject.fromObject(sysLogin.genEntityJsonStr());
			List<SysLoginRole> recs=this.hibernateHandler.getEntityListByProperty(SysLoginRole.class, "sysLogin.id", sysLogin.getId(),orderProps);
			JSONArray loginRoles=new JSONArray();
			for(SysLoginRole sysLoginRole:recs){
				loginRoles.add(JSONObject.fromObject(sysLoginRole.getSysRole().genEntityJsonStr()));
			}
			jsonRow.put("loginRoles", loginRoles);
			rows.add(jsonRow);
		}
		JSONObject json=new JSONObject();
	    json.put("total", total);
	    json.put("rows", rows);
		
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 展示
	 */
	@RequestMapping(params="action=show")
	public void show(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		JSONObject json=new JSONObject();
		if(id!=null && id.length()!=0){
			SysLogin sysLogin=this.hibernateHandler.getEntityById(SysLogin.class, id);
			if(sysLogin!=null){
				json=JSONObject.fromObject(sysLogin.genEntityJsonStr());
				List<SysLoginRole> recs=this.hibernateHandler.getEntityListByProperty(SysLoginRole.class, "sysLogin.id", id);
				JSONArray loginRoles=new JSONArray();
				for(SysLoginRole sysLoginRole:recs){
					loginRoles.add(JSONObject.fromObject(sysLoginRole.getSysRole().genEntityJsonStr()));
				}
				json.put("loginRoles", loginRoles);
			}
		}
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(json.toString());
	}
	/*
	 * 检查登录名
	 */
	@RequestMapping(params="action=checkLoginName")
	public void checkLoginName(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String loginName=request.getParameter("loginName");
		String loginId=request.getParameter("loginId");
		response.setContentType("text/plain;charset=UTF-8");
		if(this.isUniqeLoginName(loginName, loginId)){
			response.getWriter().write("true");
		}else{
			response.getWriter().write("false");
		}
	}
	/*
	 * 判断唯一登录名
	 */
	private boolean isUniqeLoginName(String loginName,String id){
		boolean unique=true;
		String sql;
		if(id==null || id.length()==0){
			sql="select count(1) from sys_login where LOGIN_NAME=?";
			int count= Kit.getObjInteger(this.sqlHandler.getOneValue(sql, new Object[]{loginName}));
			if(count!=0){
				unique=false;
			}
		}else{
			sql="select ID from sys_login where LOGIN_NAME=?";
			List<Map<String, Object>> recs=this.sqlHandler.getRecordsList(sql,new Object[]{loginName});
			if(recs.size()>1 || (recs.size()==1 && !id.equals(recs.get(0).get("ID")))){
				unique=false;
			}
		}	
		return unique;
	}
	
	/*
	 * 保存
	 */
	@RequestMapping(params="action=save")
	public void save(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String loginName=request.getParameter("loginName");
		String userName=request.getParameter("userName");
		String loginState=request.getParameter("loginState");
		String deptId=request.getParameter("deptId");
        String mailAddr=request.getParameter("mailAddr");
        String phoneNum=request.getParameter("phoneNum");

		response.setContentType("text/plain;charset=UTF-8");
		SysLogin sysLogin=null;
		SysDept sysDept=null;
		if(id!=null && id.length()!=0){
			sysLogin=this.hibernateHandler.getEntityById(SysLogin.class, id);
		}
		if(deptId!=null && deptId.length()!=0){
			sysDept=this.hibernateHandler.getEntityById(SysDept.class, deptId);
		}
		if(sysLogin==null){
			sysLogin=new SysLogin();
			String defaultPwd= CoreConstant.SYS_PROP.get("defaultPwd");
			if(defaultPwd!=null && defaultPwd.length()!=0){
				sysLogin.setLoginPwd(Kit.getMD5Str(defaultPwd));
			}
		}
		sysLogin.setLoginName(loginName);
		sysLogin.setUserName(userName);
		sysLogin.setLoginState(loginState);
		sysLogin.setSysDept(sysDept);
        sysLogin.setMailAddr(mailAddr);
        sysLogin.setPhoneNum(phoneNum);
		this.userHandler.saveSysLogin(sysLogin);
		//更新角色用户
		String[] loginRole=request.getParameterValues("loginRole");
		this.userHandler.updateLoginRoleByLoginId(sysLogin.getId(), loginRole);
		response.getWriter().write(sysLogin.genEntityJsonStr());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping(params="action=remove")
	public void remove(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String[] ids=request.getParameter("ids").split(",");
		if(ids!=null && ids.length!=0){
			for(String id:ids){
				this.userHandler.removeSysLogin(id);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	/*
	 * 移动顺序
	 */
	@RequestMapping(params="action=moveOrder")
	public void moveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String direct=request.getParameter("direct");
		String id=request.getParameter("id");
		SysLogin sysLogin=this.hibernateHandler.getEntityById(SysLogin.class, id);
		if(sysLogin!=null){
			if("top".equals(direct)){
				this.userHandler.updateSysLoginOrder(sysLogin, 3);
			}else if("up".equals(direct)){
				this.userHandler.updateSysLoginOrder(sysLogin, 2);
			}else if("down".equals(direct)){
				this.userHandler.updateSysLoginOrder(sysLogin, 1);
			}else if("bottom".equals(direct)){
				this.userHandler.updateSysLoginOrder(sysLogin, 0);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	 
	 

}
