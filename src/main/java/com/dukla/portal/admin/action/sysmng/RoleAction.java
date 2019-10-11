package com.dukla.portal.admin.action.sysmng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dukla.base.domain.*;
import com.dukla.base.jpa.dao.QueryParam;
import com.dukla.base.util.Kit;
import com.dukla.web.base.GenericAction;
import com.dukla.portal.admin.handler.UserHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *角色管理
 *欧阳亚
 *2010.12.23 
 */
@Controller
@RequestMapping("/framework/sysmng/rolemng.do")
public class RoleAction extends GenericAction {

    @Autowired
    private UserHandler userHandler;

	/*
	 * 入口
	 */
	@RequestMapping(params="action=entry")
	public String entry(Model model) throws Exception {
        model.addAttribute("title","角色");
        return "/framework/sysmng/role_list";
	}
	
	/*
	 * 列表
	 */
	@RequestMapping(params="action=list")
	public void list(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		
		String roleName=request.getParameter("roleName");
		String roleState=request.getParameter("roleState");
		
		List<QueryParam> queryParamList=new ArrayList<QueryParam>();
		if(roleName!=null && roleName.length()!=0){
			queryParamList.add(new QueryParam("and","roleName","like","%"+roleName+"%"));
		}
		if(roleState!=null && roleState.length()!=0 && !"-1".equals(roleState)){
			queryParamList.add(new QueryParam("and","roleState","=",roleState));
		}
		
		int total=this.hibernateHandler.getEntityCountByQueryParam(SysRole.class, queryParamList);
		
		String page=request.getParameter("page");//分页对象传过来的当前页号
		String size=request.getParameter("rows");//分页对象传过来的每页记录条数

        Page pageCtl=this.getPage(page,size);

		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put(sort, order);
		
		List<SysRole> list=this.hibernateHandler.getEntityListByQueryParam(SysRole.class, queryParamList, pageCtl.getStart(), pageCtl.getCount(), orderProps);
	
		JSONArray rows=new JSONArray();			
		for(SysRole sysRole:list){
			JSONObject jsonRow=JSONObject.fromObject(sysRole.genEntityJsonStr());
			int membCount=this.hibernateHandler.getEntityCountByProperty(SysLoginRole.class, "sysRole.id", sysRole.getId());
			int permitCount=this.hibernateHandler.getEntityCountByProperty(SysPermit.class, "sysRole.id", sysRole.getId());
			int permitFuncCount= Kit.getObjInteger(this.sqlHandler.getOneValue("select count(a.ID) from sys_permit_function a, (select ID from sys_permit where ROLE_ID=?) b where a.PERMIT_ID=b.ID", new Object[]{sysRole.getId()}));
			jsonRow.put("membCount", membCount);
			jsonRow.put("permitCount", String.valueOf(permitCount)+"&nbsp;|&nbsp;"+String.valueOf(permitFuncCount));
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
		SysRole sysRole=this.hibernateHandler.getEntityById(SysRole.class, id);
		response.setContentType("text/json;charset=UTF-8");
		if(sysRole!=null){
			response.getWriter().write(sysRole.genEntityJsonStr());
		}else{
			response.getWriter().write("{}");
		}
	}
	
	/*
	 * 保存
	 */
	@RequestMapping(params="action=save")
	public void save(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String roleName=request.getParameter("roleName");
		String roleState=request.getParameter("roleState");
		String roleDesc=request.getParameter("roleDesc");
		SysRole sysRole=null;
		if(id!=null && id.length()!=0){
			sysRole=this.hibernateHandler.getEntityById(SysRole.class, id);
		}
		if(sysRole==null){
			sysRole=new SysRole();			
		}
		sysRole.setRoleName(roleName);
		sysRole.setRoleState(roleState);
		sysRole.setRoleDesc(roleDesc);
		sysRole=this.userHandler.saveSysRole(sysRole);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(sysRole.genEntityJsonStr());
		
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
				this.userHandler.removeSysRole(id);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	/**
	 * 移动顺序
	 */
	@RequestMapping(params="action=moveOrder")
	public void moveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String direct=request.getParameter("direct");
		String id=request.getParameter("id");
		SysRole sysRole=this.hibernateHandler.getEntityById(SysRole.class, id);
		if(sysRole!=null){
			if("top".equals(direct)){
				this.userHandler.updateSysRoleOrder(sysRole, 3);
			}else if("up".equals(direct)){
				this.userHandler.updateSysRoleOrder(sysRole, 2);
			}else if("down".equals(direct)){
				this.userHandler.updateSysRoleOrder(sysRole, 1);
			}else if("bottom".equals(direct)){
				this.userHandler.updateSysRoleOrder(sysRole, 0);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	/**
	 * 角色成员列表
	 */
	@RequestMapping(params="action=membList")
	public void membList(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		
		String roleId=request.getParameter("roleId");
		response.setContentType("text/json;charset=UTF-8");
		if(roleId==null || roleId.length()==0){
			response.getWriter().write("{\"total\":0,\"rows\":[]}");
			return;
		}
		//取角色用户
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("sysLogin.loginOrder", "asc");
		List<SysLoginRole> loginRoles=this.hibernateHandler.getEntityListByProperty(SysLoginRole.class, "sysRole.id", roleId, orderProps);
		//取用户
		int total=this.hibernateHandler.getEntityCountAll(SysLogin.class);


        Page page=this.getPage(request.getParameter("page"),request.getParameter("rows"));

		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		orderProps.clear();
		orderProps.put(sort, order);
		List<SysLogin> list=this.hibernateHandler.getEntityListAll(SysLogin.class, page.getStart(), page.getCount(), orderProps);
	
		JSONArray rows=new JSONArray();			
		for(SysLogin sysLogin:list){
			JSONObject jsonRow=JSONObject.fromObject(sysLogin.genEntityJsonStr());
			jsonRow.put("isMemb", isMemb(sysLogin,loginRoles));
			rows.add(jsonRow);
		}
		JSONObject json=new JSONObject();
	    json.put("total", total);
	    json.put("rows", rows);
		
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(json.toString());
	}
	/*
	 * 判断是否是角色成员
	 */
	private boolean isMemb(SysLogin sysLogin,List<SysLoginRole> loginRoles){
		boolean isMemb=false;
		if(sysLogin!=null && loginRoles!=null && loginRoles.size()!=0){
			for(SysLoginRole sysLoginRole:loginRoles){
				if(sysLogin.getId().equals(sysLoginRole.getSysLogin().getId())){
					isMemb=true;
					break;
				}
			}
		}
		return isMemb;
	}
	
	/*
	 * 更新成员
	 */
	@RequestMapping(params="action=updateMemb")
	public void updateMemb(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String roleId=request.getParameter("roleId");
		String loginId=request.getParameter("loginId");
		String isMemb=request.getParameter("isMemb");
		if(roleId!=null && loginId!=null && roleId.length()!=0 && loginId.length()!=0){
			if("true".equals(isMemb)){
				this.userHandler.addLoginRole(roleId, loginId);
			}else{
				this.userHandler.removeLoginRole(roleId, loginId);
			}
		}
		
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	/*
	 * 展示权限树
	 */
	@RequestMapping(params="action=showPermitTree")
	public void showPermitTree(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		response.setContentType("text/json;charset=UTF-8");
		String roleId=request.getParameter("roleId");
		if(roleId==null || roleId.length()==0){
			response.getWriter().write("[]");
			return;
		}
		//取权限集合
		List<SysPermit> permitList=this.hibernateHandler.getEntityListByProperty(SysPermit.class, "sysRole.id", roleId);
		//取功能点权限集合
		List<SysPermitFunction> permitFuncList=this.hibernateHandler.getEntityListByProperty(SysPermitFunction.class, "sysPermit.sysRole.id", roleId);
		//生成树结构
		JSONArray json=new JSONArray();
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("moduleOrder", "asc");
		List<QueryParam> queryParamList=new ArrayList<QueryParam>();
		queryParamList.add(new QueryParam("and","upId","is","null"));
		List<SysModule> childrenList=this.hibernateHandler.getEntityListByQueryParam(SysModule.class, queryParamList, orderProps);
		if(childrenList.size()!=0){
			for(SysModule sysModule:childrenList){
				json.add(this.genPermitTreeNode(sysModule, permitList, permitFuncList));
			}
		}
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 产生权限树节点
	 */
	private JSONObject genPermitTreeNode(SysModule sysModule,List<SysPermit> permitList,List<SysPermitFunction> permitFuncList) throws Exception {
		JSONObject node=new JSONObject();
		node.put("id", "m_"+sysModule.getId());
		node.put("text", sysModule.getModuleName());
		node.put("checked", this.hasPermit(sysModule, permitList));
		JSONObject attributes=new JSONObject();
		attributes.put("type", "module");
		attributes.put("id", sysModule.getId());
		node.put("attributes", attributes);
		
		//挂孩子模块节点
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("moduleOrder", "asc");
		List<SysModule> subModleList=this.hibernateHandler.getEntityListByProperty(SysModule.class, "upId", sysModule.getId(), orderProps);
		//挂模块功能节点
		orderProps.clear();
		orderProps.put("funcOrder", "asc");
		List<SysModuleFunction> modFuncList=this.hibernateHandler.getEntityListByProperty(SysModuleFunction.class, "sysModule.id", sysModule.getId(), orderProps);
		
		if(subModleList.size()!=0){
			node.put("iconCls", "icon-window");
		}else{
			node.put("iconCls", "icon-grid");
		}
		if(subModleList.size()!=0 || modFuncList.size()!=0){
			node.put("state", "open");
			JSONArray childrenNodes=new JSONArray();
			for(SysModule subModule:subModleList){
				//递归产生孩子节点
				childrenNodes.add(genPermitTreeNode(subModule,permitList,permitFuncList));
			}
			for(SysModuleFunction sysModuleFunction:modFuncList){
				JSONObject child=new JSONObject();
				child.put("id", "f_"+sysModuleFunction.getId());
				child.put("text", sysModuleFunction.getFuncName());
				child.put("iconCls", "icon-gears");
				child.put("checked", this.hasPermitFunc(sysModuleFunction, permitFuncList));
				JSONObject attr=new JSONObject();
				attr.put("type", "func");
				attr.put("id", sysModuleFunction.getId());
				child.put("attributes", attr);
				childrenNodes.add(child);
			}
			node.put("children", childrenNodes);
		}
		return node;
	}
	
	/*
	 * 判断模块权限
	 */
	private boolean hasPermit(SysModule sysModule,List<SysPermit> permitList){
		boolean has=false;
		if(sysModule!=null && permitList!=null && permitList.size()!=0){
			for(SysPermit sysPermit:permitList){
				if(sysModule.getId().equals(sysPermit.getSysModule().getId())){
					has=true;
					break;
				}
			}
		}
		return has;
	}
	
	/*
	 * 判断功能项权限
	 */
	private boolean hasPermitFunc(SysModuleFunction sysModuleFunction,List<SysPermitFunction> permitFuncList){
		boolean has=false;
		if(sysModuleFunction!=null && permitFuncList!=null && permitFuncList.size()!=0){
			for(SysPermitFunction sysPermitFunction:permitFuncList){
				if(sysModuleFunction.getId().equals(sysPermitFunction.getSysModuleFunction().getId())){
					has=true;
					break;
				}
			}
		}
		return has;
	}
	
	/*
	 * 更新权限
	 */
	@RequestMapping(params="action=updatePermit")
	public void updatePermit(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String roleId=request.getParameter("roleId");
		String moduleId=request.getParameter("moduleId");
		String checked=request.getParameter("checked");
		if(roleId!=null && moduleId!=null && roleId.length()!=0 && moduleId.length()!=0){
			if("true".equals(checked)){
				this.userHandler.addPermit(roleId, moduleId);
			}else{
				this.userHandler.removePermit(roleId, moduleId);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	/*
	 * 更新功能点权限
	 */
	@RequestMapping(params="action=updatePermitFunc")
	public void updatePermitFunc(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String roleId=request.getParameter("roleId");
		String funcId=request.getParameter("funcId");
		String checked=request.getParameter("checked");
		if(roleId!=null && funcId!=null && roleId.length()!=0 && funcId.length()!=0){
			if("true".equals(checked)){
				this.userHandler.addFuncPermit(roleId, funcId);
			}else{
			    this.userHandler.removeFuncPermit(roleId, funcId);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	
	
	
}
