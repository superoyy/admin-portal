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
import com.dukla.web.base.CoreConstant;
import com.dukla.web.base.GenericAction;
import com.dukla.portal.admin.web.handler.DepartmentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 系统-部门管理
 * 欧阳亚
 * 2012.12.16
 */
@Controller
@RequestMapping("/framework/sysmng/deptmng.do")
public class DeptAction extends GenericAction {

    @Autowired
    DepartmentHandler departmentHandler;

	/**
	 * 进入
	 */
	@RequestMapping(params="action=entry")
	public String entry(Model model) throws Exception {
        model.addAttribute("title","部门");
        return "/framework/sysmng/dept_mng";
	}
	
	/**
	 * 列表
	 */
	@RequestMapping(params="action=list")
	public void list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put(sort, order);
		
		List<SysDept> list=this.hibernateHandler.getEntityListAll(SysDept.class, orderProps);
	
		JSONArray jsonRows=new JSONArray();
		JSONObject root=new JSONObject();			
		root.put("id", "0");
		root.put("deptName", CoreConstant.SYS_PROP.get("orgName"));
		root.put("state", "opened");
		jsonRows.add(root);
		for(SysDept rec:list){
			JSONObject jsonRow=new JSONObject();
			jsonRow.put("id", rec.getId());
			jsonRow.put("deptName", rec.getDeptName());
			jsonRow.put("deptState", rec.getDeptState());
			int count=this.hibernateHandler.getEntityCountByProperty(SysDept.class, "upId", rec.getId());
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
	
	/**
	 * 展示节点树
	 */
	@RequestMapping(params="action=showTree")
	public void showTree(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.put("deptOrder", "asc");
		List<QueryParam> queryParamList=new ArrayList<QueryParam>();
		if(id==null || id.length()==0){
			queryParamList.add(new QueryParam("and","upId","is","null"));
		}else{
			queryParamList.add(new QueryParam("and","upId","=",id));
		}
        List<SysDept> recs=this.hibernateHandler.getEntityListByQueryParam(SysDept.class, queryParamList, orderProps);
		JSONArray jsonArray=new JSONArray();			
		for(SysDept sysDept:recs){
			JSONObject json=new JSONObject();
			json.put("id", sysDept.getId());
			json.put("text", sysDept.getDeptName());
			int count=this.hibernateHandler.getEntityCountByProperty(SysDept.class, "upId", sysDept.getId());
			if(count!=0){
				json.put("state", "closed");
				json.put("children", new JSONArray());
			}
			jsonArray.add(json);
		}
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(jsonArray.toString());
	}

    /**
     * 部门成员树
     * @param id
     * @param response
     * @throws Exception
     */
    @RequestMapping(params="action=showMembTree")
    public void showMembTree(@RequestParam(value = "id",required=false) String id,
                             HttpServletResponse response)  throws Exception {
        Map<String,String> orderProps=new HashMap<String,String>();
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        List<SysLogin> membList=new ArrayList<SysLogin>();
        if(id == null || id.length() == 0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",id));
            orderProps.put("loginOrder", "asc");
            membList = this.hibernateHandler.getEntityListByProperty(SysLogin.class,"sysDept.id",id,orderProps);
        }
        orderProps.clear();
        orderProps.put("deptOrder", "asc");
        List<SysDept> recs=this.hibernateHandler.getEntityListByQueryParam(SysDept.class, queryParamList, orderProps);
        JSONArray jsonArray=new JSONArray();
        for(SysDept sysDept:recs){
            JSONObject json=new JSONObject();
            json.put("id", sysDept.getId());
            json.put("text", sysDept.getDeptName());
            json.put("iconCls", "icon-users");
            JSONObject attributes=new JSONObject();
            attributes.put("type","dept");
            json.put("attributes",attributes);
            int count=this.hibernateHandler.getEntityCountByProperty(SysDept.class, "upId", sysDept.getId());
            int mcount=this.hibernateHandler.getEntityCountByProperty(SysLogin.class,"sysDept.id",sysDept.getId());
            if(count!=0 || mcount!=0){
                json.put("state", "closed");
                json.put("children", new JSONArray());
            }
            jsonArray.add(json);
        }
        for(SysLogin sysLogin:membList){
            JSONObject memb=new JSONObject();
            memb.put("id","m_"+sysLogin.getId());
            memb.put("text",sysLogin.getUserName());
            JSONObject attributes=new JSONObject();
            attributes.put("type","memb");
            memb.put("iconCls", "icon-user");
            attributes.put("mailAddr",sysLogin.getMailAddr());
            attributes.put("phoneNum",sysLogin.getPhoneNum());
            memb.put("attributes",attributes);
            jsonArray.add(memb);
        }


        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(jsonArray.toString());

    }


	
	/*
	 * 展示节点
	 */
	@RequestMapping(params="action=show")
	public void show(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		response.setContentType("text/json;charset=UTF-8");
		if(id==null || id.length()==0){
			response.getWriter().write((new JSONObject()).toString());
			return;
		}
		SysDept sysDept=this.hibernateHandler.getEntityById(SysDept.class, id);
		JSONObject json=new JSONObject();			
		if(sysDept!=null){
			json=JSONObject.fromObject(sysDept.genEntityJsonStr());
		}
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 保存节点
	 */
	@RequestMapping(params="action=save")
	public void save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String upId=request.getParameter("upId");
		String deptNo=request.getParameter("deptNo");
		String deptName=request.getParameter("deptName");
		String deptDesc=request.getParameter("deptDesc");
		String deptState=request.getParameter("deptState");
		SysDept sysDept;
		if(id !=null && id.length()!=0){
			//更新
			sysDept=this.hibernateHandler.getEntityById(SysDept.class, id);
			sysDept.setDeptDesc(deptDesc);
			sysDept.setDeptName(deptName);
			sysDept.setDeptNo(deptNo);
			sysDept.setDeptState(deptState);
		}else{
			//添加
			sysDept=new SysDept();
			if("0".equals(upId)){
				upId=null;
			}
			sysDept.setUpId(upId);
			sysDept.setDeptName("新部门");
		}
		sysDept=this.departmentHandler.saveSysDept(sysDept);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(sysDept.genEntityJsonStr());
	}
	/*
	 * 判断deptNo是否唯一
	 */
	@RequestMapping(params="action=isUqDeptNo")
	public void isUqDeptNo(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		String deptNo=request.getParameter("deptNo");
		String uq= checkUqDeptNo(deptNo,id) ? "true" : "false";
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(uq);
	}
	/*
	 * 判断deptNo是否唯一
	 */
	private boolean checkUqDeptNo(String deptNo,String id) throws Exception {
		boolean uq=true;
		if(id==null || id.length()==0){
			int count=this.hibernateHandler.getEntityCountByProperty(SysDept.class, "deptNo", deptNo);
			if(count!=0){
				uq=false;
			}
		}else{
			List<SysDept> recs=this.hibernateHandler.getEntityListByProperty(SysDept.class, "deptNo", deptNo);
			if(recs.size()==1 && !recs.get(0).getId().equals(id)){
				uq=false;
			}
		}
		return uq;
	}
	
	
	/*
	 * 删除节点
	 */
	@RequestMapping(params="action=remove")
	public void remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		if(id !=null && id.length()!=0){
			this.departmentHandler.removeSysDept(id);
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	/*
	 * 移动序号
	 */
	@RequestMapping(params="action=moveOrder")
	public void moveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String direct=request.getParameter("direct");
		String id=request.getParameter("id");
		if(id!=null && id.length()!=0){
			SysDept sysDept=this.hibernateHandler.getEntityById(SysDept.class, id);
			if("top".equals(direct)){
				this.departmentHandler.updateSysDeptOrder(sysDept, 0);
			}else if("up".equals(direct)){
				this.departmentHandler.updateSysDeptOrder(sysDept, 1);
			}else if("down".equals(direct)){
				this.departmentHandler.updateSysDeptOrder(sysDept, 2);
			}else if("bottom".equals(direct)){
				this.departmentHandler.updateSysDeptOrder(sysDept, 3);
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}
	
	
	/*
	 * 成员列表
	 */
	@RequestMapping(params="action=listMemb")
	public void listMemb(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String deptId=request.getParameter("deptId");
		//取用户
		int total=this.hibernateHandler.getEntityCountAll(SysLogin.class);
		
		String page=request.getParameter("page");//分页对象传过来的当前页号
		String size=request.getParameter("rows");//分页对象传过来的每页记录条数

        Page pageCtl=this.getPage(page,size);

		String sort=request.getParameter("sort");//分页对象传过来的排序字段
		String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
		Map<String,String> orderProps=new HashMap<String,String>();
		orderProps.clear();
		orderProps.put(sort, order);
		List<SysLogin> list=this.hibernateHandler.getEntityListAll(SysLogin.class, pageCtl.getStart(), pageCtl.getCount(), orderProps);
	
		JSONArray rows=new JSONArray();			
		for(SysLogin sysLogin:list){
			JSONObject jsonRow=JSONObject.fromObject(sysLogin.genEntityJsonStr());
			jsonRow.put("isMemb", (deptId!=null && deptId.length()!=0 && sysLogin.getSysDept()!=null && sysLogin.getSysDept().getId().equals(deptId)));
			rows.add(jsonRow);
		}
		JSONObject json=new JSONObject();
	    json.put("total", total);
	    json.put("rows", rows);
		
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(json.toString());
	}
	
	/*
	 * 更新成员
	 */
	@RequestMapping(params="action=updateMemb")
	public void updateMemb(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
		String deptId=request.getParameter("deptId");
		String loginId=request.getParameter("loginId");
		String isMemb=request.getParameter("isMemb");
		if(deptId!=null && loginId!=null && deptId.length()!=0 && loginId.length()!=0){
			if("true".equals(isMemb)){
				this.sqlHandler.execute("update sys_login set DEPT_ID=? where ID=?", new Object[]{deptId,loginId});
			}else{
				this.sqlHandler.execute("update sys_login set DEPT_ID=NULL where DEPT_ID=? and ID=?", new Object[]{deptId,loginId});
			}
		}
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("ok");
	}	
	
	
}
