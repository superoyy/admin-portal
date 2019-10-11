package com.dukla.portal.admin.action.sysmng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dukla.base.domain.*;
import com.dukla.web.base.GenericAction;
import com.dukla.portal.admin.handler.ModuleHandler;
import com.dukla.portal.admin.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 系统-模块管理
 * 欧阳亚
 * 2014.12.19
 */
@Controller
@RequestMapping("/framework/sysmng/modulemng.do")
public class ModuleAction extends GenericAction {

    @Autowired
    private ModuleHandler moduleHandler;

    @Autowired
    private UserHandler userHandler;

    /*
	 * 进入模块管理
	 */
	@RequestMapping(params="action=entry")
	public String entry(Model model) throws Exception {
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("roleOrder", "asc");
		List<SysRole> recs=this.hibernateHandler.getEntityListAll(SysRole.class, orderProps);
        model.addAttribute("roles",recs);
        return "/framework/sysmng/module_mng";
	}
	
	/*
	 * 模块列表
	 */
	@RequestMapping(params="action=list")
	public void list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put(sort, order);
		
		List<SysModule> list=this.hibernateHandler.getEntityListAll(SysModule.class, orderProps);
	
		JSONArray jsonRows=new JSONArray();
		JSONObject root=new JSONObject();			
		root.put("id", "0");
		root.put("moduleName", "根节点");
		root.put("state", "opened");
		jsonRows.add(root);
		for(SysModule rec:list){
			JSONObject jsonRow=new JSONObject();
			jsonRow.put("id", rec.getId());
			jsonRow.put("moduleName", rec.getModuleName());
			int count=this.hibernateHandler.getEntityCountByProperty(SysModule.class, "upId", rec.getId());
			if(rec.getUpId()!=null){
				jsonRow.put("_parentId",rec.getUpId());
			}else{
				jsonRow.put("_parentId","0");
			}
			if(count!=0){
				jsonRow.put("state", "opened");
			}
			jsonRows.add(jsonRow);
		}
	    JSONObject json=new JSONObject();
	    json.put("rows", jsonRows);
		
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 展示功能节点
	 */
	@RequestMapping(params="action=show")
	public void show(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		response.setContentType("text/json;charset=UTF-8");
		if(id==null || id.length()==0){
			response.getWriter().write((new JSONObject()).toString());
			return;
		}
		SysModule sysModule=this.hibernateHandler.getEntityById(SysModule.class, id);
		JSONObject json=new JSONObject();
		if(sysModule!=null){
			json=JSONObject.fromObject(sysModule);
			List<SysPermit> recs=this.hibernateHandler.getEntityListByProperty(SysPermit.class, "sysModule.id", sysModule.getId(),null);
			JSONArray jsonRows=new JSONArray();
			for(SysPermit sysPermit:recs){
				JSONObject jsonRow=new JSONObject();
				jsonRow.put("roleId", sysPermit.getSysRole().getId());
				jsonRow.put("roleName", sysPermit.getSysRole().getRoleName());
				jsonRows.add(jsonRow);
			}
			json.put("permit",jsonRows);
		}
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 保存节点
	 */
	@RequestMapping(method = RequestMethod.POST,params="action=save")
	public void save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String moduleName=request.getParameter("moduleName");
		String moduleDesc=request.getParameter("moduleDesc");
		String moduleState=request.getParameter("moduleState");
		String upId=request.getParameter("upId");
        String url=request.getParameter("url");
        String icon=request.getParameter("icon");
		String[] permitRole=request.getParameterValues("permitRole");
		SysModule sysModule;
		if(id !=null && id.length()!=0){
			//更新
			sysModule=this.hibernateHandler.getEntityById(SysModule.class, id);
			sysModule.setModuleName(moduleName);
			sysModule.setModuleDesc(moduleDesc);
			sysModule.setModuleState(moduleState);
            sysModule.setUrl(url);
            sysModule.setIcon(icon);
		}else{
			//添加
			sysModule=new SysModule();
			if("0".equals(upId)){
				upId=null;
			}
			sysModule.setUpId(upId);
			sysModule.setModuleName("新功能");
			sysModule.setModuleState("0");
            sysModule.setIcon("icon-cogs");
		}
		sysModule=this.moduleHandler.saveSysModule(sysModule);
		//更改权限
		this.userHandler.updatePermitByModuleId(sysModule.getId(), permitRole);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(sysModule.genEntityJsonStr());
	}
	/*
	 * 删除节点
	 */
	@RequestMapping(params="action=remove")
	public void remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		if(id !=null && id.length()!=0){
			this.moduleHandler.removeSysModule(id);
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	/*
	 * 移动模块序号
	 */
	@RequestMapping(params="action=moveOrder")
	public void moveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String direct=request.getParameter("direct");
		String id=request.getParameter("id");
		if(id!=null && id.length()!=0){
			SysModule sysModule=this.hibernateHandler.getEntityById(SysModule.class, id);
			if("top".equals(direct)){
				this.moduleHandler.updateSysModuleOrder(sysModule, 0);
			}else if("up".equals(direct)){
				this.moduleHandler.updateSysModuleOrder(sysModule, 1);
			}else if("down".equals(direct)){
				this.moduleHandler.updateSysModuleOrder(sysModule, 2);
			}else if("bottom".equals(direct)){
				this.moduleHandler.updateSysModuleOrder(sysModule, 3);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	
	/*
	 * 得到功能项
	 */
	@RequestMapping(params="action=listModuleFunc")
	public void listModuleFunc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String moduleId=request.getParameter("moduleId");
		
		response.setContentType("text/json;charset=UTF-8");
		if(moduleId==null || moduleId.length()==0){
		    JSONObject json=new JSONObject();
		    json.put("total", 0);
		    json.put("rows", new JSONArray());
			response.getWriter().write(json.toString());
			return;
		}
		
		int total=this.hibernateHandler.getEntityCountByProperty(SysModuleFunction.class, "sysModule.id", moduleId);
        Page page=this.getPage(request.getParameter("page"),request.getParameter("rows"));
		//排序
		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put(sort, order);
		//查询
		List<SysModuleFunction> list=this.hibernateHandler.getEntityListByProperty(SysModuleFunction.class, "sysModule.id", moduleId, page.getStart(), page.getCount(), orderProps);
		JSONArray jsonRows=new JSONArray();
		for(SysModuleFunction rec:list){
			JSONObject jsonRow=JSONObject.fromObject(rec);
			orderProps.clear();
			orderProps.put("sysPermit.sysRole.roleOrder", "asc");
			List<SysPermitFunction> permitFunctions=this.hibernateHandler.getEntityListByProperty(SysPermitFunction.class, "sysModuleFunction.id", rec.getId(), orderProps);
			JSONArray jsonPermitFunctions=new JSONArray();
			for(SysPermitFunction permitFunction:permitFunctions){
				jsonPermitFunctions.add(JSONObject.fromObject(permitFunction.getSysPermit().getSysRole().genEntityJsonStr()));
			}
			jsonRow.put("permitRols", jsonPermitFunctions);
			
			jsonRows.add(jsonRow);
		}
	    JSONObject json=new JSONObject();
	    json.put("total", total);
	    json.put("rows", jsonRows);
		
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 展示功能项
	 */
	@RequestMapping(params="action=showModuleFunc")
	public void showModuleFunc(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		response.setContentType("text/json;charset=UTF-8");
		if(id==null || id.length()==0){
			response.getWriter().write((new JSONObject()).toString());
			return;
		}
		
		SysModuleFunction sysModuleFunction=this.hibernateHandler.getEntityById(SysModuleFunction.class, id);
		JSONObject json=new JSONObject();			
		if(sysModuleFunction!=null){
			json=JSONObject.fromObject(sysModuleFunction);
			List<SysPermitFunction> recs=this.hibernateHandler.getEntityListByProperty(SysPermitFunction.class, "sysModuleFunction.id", sysModuleFunction.getId(),null);
			JSONArray jsonRows=new JSONArray();
			for(SysPermitFunction sysPermitFunction:recs){
				JSONObject jsonRow=new JSONObject();
				jsonRow.put("roleId", sysPermitFunction.getSysPermit().getSysRole().getId());
				jsonRow.put("roleName", sysPermitFunction.getSysPermit().getSysRole().getRoleName());
				jsonRows.add(jsonRow);
			}
			json.put("permit",jsonRows);
		}
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 保存功能项
	 */
	@RequestMapping(params="action=saveModuleFunc")
	public void saveModuleFunc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String moduleId=request.getParameter("moduleId");		
		String funcName=request.getParameter("funcName");
		String funcDesc=request.getParameter("funcDesc");
		String url=request.getParameter("url");
		String[] permitFuncRole=request.getParameterValues("permitFuncRole");
		SysModuleFunction sysModuleFunction=new SysModuleFunction();
		if(id !=null && id.length()!=0){
			//更新
			sysModuleFunction= this.hibernateHandler.getEntityById(SysModuleFunction.class, id);
		}else{
			//添加
			SysModule sysModule=new SysModule();
			sysModule.setId(moduleId);
			sysModuleFunction.setSysModule(sysModule);
		}
		sysModuleFunction.setFuncName(funcName);
		sysModuleFunction.setUrl(url);
		sysModuleFunction.setFuncDesc(funcDesc);
		sysModuleFunction=this.moduleHandler.saveSysModuleFunction(sysModuleFunction);
		//更新权限
		this.userHandler.updateFuncPermitByFuncId(sysModuleFunction.getId(), permitFuncRole);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(sysModuleFunction.genEntityJsonStr());
	}
	/*
	 * 删除功能项
	 */
	@RequestMapping(params="action=removeModuleFunc")
	public void removeModuleFunc(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] ids=request.getParameter("ids").split(",");
		if(ids!=null && ids.length!=0){
			for(String id:ids){
				this.moduleHandler.removeSysModuleFunction(id);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	/*
	 * 移动功能项序号
	 */
	@RequestMapping(params="action=moveModuleFuncOrder")
	public void moveModuleFuncOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String direct=request.getParameter("direct");
		String id=request.getParameter("id");
		if(id!=null && id.length()!=0){
			SysModuleFunction sysModuleFunction=this.hibernateHandler.getEntityById(SysModuleFunction.class, id);
			if("top".equals(direct)){
				this.moduleHandler.updateSysModuleFunctionOrder(sysModuleFunction, 3);
			}else if("up".equals(direct)){
				this.moduleHandler.updateSysModuleFunctionOrder(sysModuleFunction, 2);
			}else if("down".equals(direct)){
				this.moduleHandler.updateSysModuleFunctionOrder(sysModuleFunction, 1);
			}else if("bottom".equals(direct)){
				this.moduleHandler.updateSysModuleFunctionOrder(sysModuleFunction, 0);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
}
