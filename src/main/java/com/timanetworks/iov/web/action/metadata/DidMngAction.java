package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.CustomDid;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dyl on 2016/12/8.
 */
@Controller
@RequestMapping("/iov/metadata/did")
public class DidMngAction extends GenericAction {

    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/did_list";
    }

    /**
     * 获取
     */
    @RequestMapping(method = RequestMethod.POST,value = "/get")
    @ResponseBody
    public Map<String,Object> get(@RequestParam(value = "id",required = true) String id){
        CustomDid did=this.hibernateHandler.getEntityById(CustomDid.class,id);
        Map<String,Object> rs=new HashMap<>();
        Gson gson=new Gson();
        if(did!=null){
            rs=gson.fromJson(did.genEntityJsonStr(),Map.class);
        }
        return rs;
    }

    /**
     * 检查唯一
     */
    @RequestMapping(method = RequestMethod.POST,value = "/check",produces = "text/plain")
    @ResponseBody
    public String check(@RequestParam(value = "id",required = false) String id,
                        @RequestParam(value = "code",required = true) int code){
        String unique="true";
        List<CustomDid> dids=this.hibernateHandler.getEntityListByProperty(CustomDid.class,"code",code);
        if(id != null){
            CustomDid  old=this.hibernateHandler.getEntityById(CustomDid.class, id);
            if(dids.size() > 0){
                unique = old.getId().equals(dids.get(0).getId()) ? "true" : "false";
            }
        }else{
            unique = dids.size() > 0 ? "false" : "true";
        }
        return unique;
    }

    /**
     * 删除
     */
    @RequestMapping(method = RequestMethod.POST,value = "/remove")
    @ResponseBody
    public void remove(@RequestParam(value = "ids",required = true) String ids){
        String[] idArray=ids.split(",");
        if(idArray.length!=0){
            StringBuilder sql=new StringBuilder();
            List<String> objectParams=new ArrayList<>();
            sql.append("delete from custom_did where id in (");
            for(int i=0;i<idArray.length;i++){
                if(i != idArray.length-1){
                    sql.append("?,");
                }else{
                    sql.append("?");
                }
                objectParams.add(idArray[i]);
            }
            sql.append(")");
            this.sqlHandler.execute(sql.toString(),objectParams.toArray());
        }

    }

    /**
     * 保存
     */
    @RequestMapping(method = RequestMethod.POST,value = "/save")
    @ResponseBody
    public void save(@RequestParam(value = "id",required = false) String id,
                     @RequestParam(value = "code",required = true) int code,
                     @RequestParam(value = "showNameEn",required = true) String showNameEn,
                     @RequestParam(value = "showNameCn",required = false) String showNameCn,
                     @RequestParam(value = "remark",required = false) String remark){
        CustomDid  did = null;
        if(id != null){
            did=this.hibernateHandler.getEntityById(CustomDid.class,id);
        }
        if(did == null){
            did=new CustomDid();
        }
        did.setCode(code);
        did.setShowNameEn(showNameEn);
        did.setShowNameCn(showNameCn);
        did.setRemark(remark);
        if(did.getId()!=null){
            this.hibernateHandler.modifyEntity(did);
        }else{
            this.hibernateHandler.addEntity(did);
        }
    }


    /**
     * 信号查询
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "code",required = false) String code,
                            @RequestParam(value = "showNameEn",required = false) String showNameEn,
                            @RequestParam(value = "showNameCn",required = false) String showNameCn,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<QueryParam> queryParamList=new ArrayList<>();
        if(Kit.isInt(code)){
            queryParamList.add(new QueryParam("and","code","=", Integer.parseInt(code)));
        }
        if(showNameEn!=null && showNameEn.length()!=0){
            queryParamList.add(new QueryParam("and","showNameEn","like","%"+showNameEn+"%"));
        }
        if(showNameCn!=null && showNameCn.length()!=0){
            queryParamList.add(new QueryParam("and","showNameCn","like","%"+showNameCn+"%"));
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(CustomDid.class, queryParamList));
        List<CustomDid> rs=this.hibernateHandler.getEntityListByQueryParam(CustomDid.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        for(CustomDid did:rs){
            String jsonStr=did.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }

        return queryResult;
    }

    /**
     * 查询Did
     */
    @RequestMapping(method = RequestMethod.POST,value = "/queryDid")
    @ResponseBody
    public QueryResult queryDid(@RequestParam(value = "q",required = false) String keywords){
        QueryResult queryResult=new QueryResult();
        queryResult.getOrders().put("code", "asc");
        List<CustomDid> rs;
        if(keywords!=null && keywords.length()>0){
            List<QueryParam> queryParamList=new ArrayList<>();
            if(Kit.isInt(keywords)){
                queryParamList.add(new QueryParam("and (","code","=",Integer.parseInt(keywords)));
                queryParamList.add(new QueryParam("or", "showNameCn", "like", "%"+keywords+"%"));
                queryParamList.add(new QueryParam("or", "showNameEn", "like", "%"+keywords+"%"));
                queryParamList.add(new QueryParam(") and", "id", "is", "not null"));

            }else{
                queryParamList.add(new QueryParam("and (", "showNameCn", "like", "%"+keywords+"%"));
                queryParamList.add(new QueryParam("or", "showNameEn", "like", "%"+keywords+"%"));
                queryParamList.add(new QueryParam(") and", "id", "is", "not null"));
            }
            rs=this.hibernateHandler.getEntityListByQueryParam(CustomDid.class, queryParamList,queryResult.getOrders());
        }else{
            rs=this.hibernateHandler.getEntityListAll(CustomDid.class, queryResult.getOrders());
        }
        Gson gson=new Gson();
        for(CustomDid did:rs){
            String jsonStr=did.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }
        return queryResult;
    }




}


