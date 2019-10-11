package com.timanetworks.iov.web.action.sysmng;

import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.SysCodes;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.CoreConstant;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.RequestCheck;
import com.timanetworks.iov.web.handler.CodesHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 11/9/16.
 */
@Controller
@RequestMapping("/framework/sysmng/codesmng.do")
public class CodesAction extends GenericAction {
    
    @Autowired
    CodesHandler codesHandler;
    /**
     * 入口
     */
    @RequestMapping(params="action=entry")
    public String entry(@RequestParam("codeType") String codeType,
                        @RequestParam("viewType") String viewType,
                        Model model) throws Exception {
        String target="";
        model.addAttribute("codeType",codeType);
        if("form".equals(viewType)){//使用表单展现
            List<SysCodes> recs=this.codesHandler.getSysCodesByType(codeType);
            model.addAttribute("sysCodesList",recs);
            target="/framework/sysmng/codes_form";
        }else if("grid".equals(viewType)){//表格展现
            target="/framework/sysmng/codes_list";
        }else if("tree".equals(viewType)){//树形展现
            target="/framework/sysmng/codes_tree";
        }
        return target;
    }
    /**
     * 查询列表
     */
    @RequestMapping(params="action=list")
    public void list(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {

        String upId=request.getParameter("upId");
        String codeType=request.getParameter("codeType");
        String codeKey=request.getParameter("codeKey");
        String codeName=request.getParameter("codeName");
        String codeValue=request.getParameter("codeValue");
        String codeState=request.getParameter("codeState");
        String codeDesc=request.getParameter("codeDesc");
        String maintainType=request.getParameter("maintainType");
        String remainField=request.getParameter("remainField");

        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        if(upId!=null && upId.length()!=0){
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        if(codeType!=null && codeType.length()!=0){
            queryParamList.add(new QueryParam("and","codeType","=",codeType));
        }
        if(codeKey!=null && codeKey.length()!=0){
            queryParamList.add(new QueryParam("and","codeKey","like","%"+codeKey+"%"));
        }
        if(codeName!=null && codeName.length()!=0){
            queryParamList.add(new QueryParam("and","codeName","like","%"+codeName+"%"));
        }
        if(codeValue!=null && codeValue.length()!=0){
            queryParamList.add(new QueryParam("and","codeValue","like","%"+codeValue+"%"));
        }
        if(codeState!=null && codeState.length()!=0){
            queryParamList.add(new QueryParam("and","codeState","=",codeState));
        }
        if(codeDesc!=null && codeDesc.length()!=0){
            queryParamList.add(new QueryParam("and","codeDesc","like","%"+codeDesc+"%"));
        }
        if(maintainType!=null && maintainType.length()!=0){
            queryParamList.add(new QueryParam("and","maintainType","=",maintainType));
        }
        if(remainField!=null && remainField.length()!=0){
            queryParamList.add(new QueryParam("and","remainField","=",remainField));
        }

        int total=this.hibernateHandler.getEntityCountByQueryParam(SysCodes.class, queryParamList);

        Page page=this.getPage(request.getParameter("page"),request.getParameter("rows"));

        String sort=request.getParameter("sort");//分页对象传过来的排序字段
        String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put(sort, order);

        List<SysCodes> list=this.hibernateHandler.getEntityListByQueryParam(SysCodes.class, queryParamList, page.getStart(), page.getCount(), orderProps);

        JSONArray rows=new JSONArray();
        for(SysCodes rec:list){
            rows.add(JSONObject.fromObject(rec.genEntityJsonStr()));
        }
        JSONObject json=new JSONObject();
        json.put("total", total);
        json.put("rows", rows);

        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(json.toString());
    }

    /**
     * 树列表
     */
    @RequestMapping(params="action=treeList")
    public void treeList(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {

        String codeType=request.getParameter("codeType");

        String sort=request.getParameter("sort");//分页对象传过来的排序字段
        String order=request.getParameter("order");//分页对象传过来的排序类型asc|desc

        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put(sort, order);

        List<SysCodes> list=this.hibernateHandler.getEntityListByProperty(SysCodes.class, "codeType", codeType,orderProps);

        JSONArray jsonRows=new JSONArray();
        JSONObject root=new JSONObject();
        root.put("id", "0");
        root.put("codeName", "根节点");
        root.put("state", "opened");
        jsonRows.add(root);
        for(SysCodes rec:list){
            JSONObject jsonRow=new JSONObject();
            jsonRow.put("id", rec.getId());
            jsonRow.put("codeName", rec.getCodeName());
            int count=this.hibernateHandler.getEntityCountByProperty(SysCodes.class, "upId", rec.getId());
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
     * 展示
     */
    @RequestMapping(params="action=show")
    public void show(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        String id=request.getParameter("id");
        SysCodes sysCodes=this.hibernateHandler.getEntityById(SysCodes.class,id);
        response.setContentType("text/json;charset=UTF-8");
        if(sysCodes!=null){
            response.getWriter().write(sysCodes.genEntityJsonStr());
        }else{
            response.getWriter().write("{}");
        }
    }
    /**
     * 行添加
     */
    @RequestMapping(params="action=add")
    //@RequestCheck(checkSession = true,checkPermit = true)
    public void add(HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        String upId=request.getParameter("upId");
        String codeType=request.getParameter("codeType");
        String codeKey=request.getParameter("codeKey");
        String codeName=request.getParameter("codeName");
        String codeValue=request.getParameter("codeValue");
        String codeValueType=request.getParameter("codeValueType");
        String codeState=request.getParameter("codeState");
        String codeDesc=request.getParameter("codeDesc");
        String maintainType=request.getParameter("maintainType");
        String remainField=request.getParameter("remainField");
        if(codeName==null || codeName.length()==0){
            codeName="请填写";
        }
        if(codeValue==null || codeValue.length()==0){
            codeValue="请填写";
        }
        if("0".equals(upId)){
            upId=null;
        }
        SysCodes sysCodes=this.saveSysCodes(null, upId, codeType, codeKey, codeName, codeValue, codeValueType, codeState, codeDesc, maintainType, remainField);
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(sysCodes.genEntityJsonStr());
    }
    /**
     * 删除
     */
    @RequestMapping(params="action=remove")
    //@RequestCheck(checkSession = true,checkPermit = true)
    public void remove(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        String[] ids=request.getParameter("ids").split(",");
        if(ids!=null && ids.length!=0){
            for(String id:ids){
                this.codesHandler.removeSysCodes(id);
            }
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("ok");
    }
    /**
     * 保存表单类型码值
     */
    @RequestMapping(params="action=saveForm")
    //@RequestCheck(checkSession = true,checkPermit = true)
    public void saveForm(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        Map<String,String[]> params=request.getParameterMap();
        for(String param:params.keySet()){
            if(param.startsWith("codeId_")){
                String id=param.substring(7);
                String codeValue=request.getParameter(param);
                String sql="update sys_codes set CODE_VALUE=? where ID=?";
                this.sqlHandler.execute(sql, new Object[]{codeValue,id});
                //更新系统全局变量
                sql="select CODE_KEY,CODE_TYPE,CODE_VALUE from sys_codes where ID=?";
                Map<String,Object> codes=this.sqlHandler.getOneRecord(sql, new Object[]{id});
                if(codes!=null && "SYS_PROP".equals(codes.get("CODE_TYPE"))){
                    CoreConstant.SYS_PROP.put(Kit.getObjStr(codes.get("CODE_KEY")), Kit.getObjStr(codes.get("CODE_VALUE")));
                }
            }
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("ok");
    }
    /**
     * 保存
     */
    @RequestMapping(params="action=save")
    //@RequestCheck(checkSession = true,checkPermit = true)
    public void save(HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        String id=request.getParameter("id");
        String upId=request.getParameter("upId");
        String codeType=request.getParameter("codeType");
        String codeKey=request.getParameter("codeKey");
        String codeValue=request.getParameter("codeValue");
        String codeName=request.getParameter("codeName");
        String codeValueType=request.getParameter("codeValueType");
        String codeState=request.getParameter("codeState");
        String codeDesc=request.getParameter("codeDesc");
        String maintainType=request.getParameter("maintainType");
        String remainField=request.getParameter("remainField");
        SysCodes sysCodes=this.saveSysCodes(id, upId, codeType, codeKey, codeName, codeValue, codeValueType, codeState, codeDesc, maintainType, remainField);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(sysCodes.genEntityJsonStr());
    }
    /**
     * 批量保存
     */
    @RequestMapping(params="action=batchSave")
    //@RequestCheck(checkSession = true,checkPermit = true)
    public void batchSave(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        String size=request.getParameter("size");
        if(size!=null && size.length()!=0 && Kit.isNum(size)){
            int length=Integer.parseInt(size);
            for(int i=0;i<length;i++){
                String p="sysCodes["+String.valueOf(i)+"]";
                String id=request.getParameter(p+"[id]");
                String upId=request.getParameter(p+"[upId]");
                String codeType=request.getParameter(p+"[codeType]");
                String codeKey=request.getParameter(p+"[codeKey]");
                String codeValue=request.getParameter(p+"[codeValue]");
                String codeName=request.getParameter(p+"[codeName]");
                String codeValueType=request.getParameter(p+"[codeValueType]");
                String codeState=request.getParameter(p+"[codeState]");
                String codeDesc=request.getParameter(p+"[codeDesc]");
                String maintainType=request.getParameter(p+"[maintainType]");
                String remainField=request.getParameter(p+"[remainField]");
                this.saveSysCodes(id, upId, codeType, codeKey, codeName, codeValue, codeValueType, codeState, codeDesc, maintainType, remainField);
            }
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("ok");
    }
    /**
     * 保存SysCodes
     */
    private SysCodes saveSysCodes(String id,String upId,String codeType,String codeKey,String codeName,String codeValue,String codeValueType,String codeState,String codeDesc,String maintainType,String remainField) throws Exception {
        SysCodes sysCodes=new SysCodes();
        if(id!=null && id.length()!=0){
            sysCodes=this.hibernateHandler.getEntityById(SysCodes.class,id);
        }
        if(upId!=null && upId.length()!=0){
            sysCodes.setUpId(upId);
        }
        if(codeType!=null && codeType.length()!=0){
            sysCodes.setCodeType(codeType);
        }
        if(codeKey!=null && codeKey.length()!=0){
            sysCodes.setCodeKey(codeKey);
        }
        if(codeName!=null && codeName.length()!=0){
            sysCodes.setCodeName(codeName);
        }
        if(codeValue!=null){
            sysCodes.setCodeValue(codeValue);
        }
        if(codeValueType!=null && codeValueType.length()!=0){
            sysCodes.setCodeValueType(codeValueType);
        }
        if(codeState!=null && codeState.length()!=0){
            sysCodes.setCodeState(codeState);
        }
        if(codeDesc!=null && codeDesc.length()!=0){
            sysCodes.setCodeDesc(codeDesc);
        }
        if(maintainType!=null && maintainType.length()!=0){
            sysCodes.setMaintainType(maintainType);
        }
        if(remainField!=null && remainField.length()!=0){
            sysCodes.setRemainField(remainField);
        }
        if(this.checkUqCodeKey(codeType, codeKey, id)){
            sysCodes=this.codesHandler.saveSysCodes(sysCodes);
        }
        return sysCodes;
    }


    /**
     * 移动序号
     */
    @RequestMapping(params="action=moveOrder")
    public void moveOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String direct=request.getParameter("direct");
        String id=request.getParameter("id");
        SysCodes sysCodes=this.hibernateHandler.getEntityById(SysCodes.class,id);
        if(sysCodes!=null){
            if("top".equals(direct)){
                this.codesHandler.updateSysCodesOrder(sysCodes, 3);
            }else if("up".equals(direct)){
                this.codesHandler.updateSysCodesOrder(sysCodes, 2);
            }else if("down".equals(direct)){
                this.codesHandler.updateSysCodesOrder(sysCodes, 1);
            }else if("bottom".equals(direct)){
                this.codesHandler.updateSysCodesOrder(sysCodes, 0);
            }
        }
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("ok");
    }

    /**
     * 判断codeKey是否唯一
     */
    @RequestMapping(params="action=isUqCodeKey")
    public void isUqCodeKey(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String id=request.getParameter("id");
        String codeType=request.getParameter("codeType");
        String codeKey=request.getParameter("codeKey");
        String uq= checkUqCodeKey(codeType,codeKey,id) ? "true" : "false";
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(uq);
    }
    /**
     * 判断codeKey是否唯一
     */
    private boolean checkUqCodeKey(String codeType,String codeKey,String id){
        boolean uq=true;
        if(id==null || id.length()==0){
            SysCodes sysCodes=this.codesHandler.getSysCodesByKey(codeType, codeKey);
            if(sysCodes!=null){
                uq=false;
            }
        }else{
            SysCodes sysCodes=this.codesHandler.getSysCodesByKey(codeType, codeKey);
            if(sysCodes!=null && !sysCodes.getId().equals(id)){
                uq=false;
            }
        }
        return uq;
    }

    /**
     * 根据键取codes
     */
    @RequestMapping(params="action=getCodesByKey")
    public void getCodesByKey(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String codeType=request.getParameter("codeType");
        String codeKey=request.getParameter("codeKey");
        SysCodes sysCodes=this.codesHandler.getSysCodesByKey(codeType, codeKey);
        response.setContentType("text/json;charset=UTF-8");
        if(sysCodes != null){
            response.getWriter().write(sysCodes.genEntityJsonStr());
        }else{
            response.getWriter().write("{}");
        }
    }
    
}
