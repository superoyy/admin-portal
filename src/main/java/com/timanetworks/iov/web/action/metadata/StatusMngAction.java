package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.GlobalStatus;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dyl on 2016/12/7.
 */
@Controller
@RequestMapping("/iov/metadata/status")
public class StatusMngAction extends GenericAction {

    /**
     * 进入模块管理
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/status_list";
    }

    /**
     * 获取
     */
    @RequestMapping(method = RequestMethod.POST,value = "/get")
    @ResponseBody
    public Map<String,String> get(@RequestParam(value = "id",required = true) String id){
        GlobalStatus status=this.hibernateHandler.getEntityById(GlobalStatus.class,id);
        Map<String,String> rs=new HashMap<>();
        Gson gson=new Gson();
        if(status!=null){
            rs=gson.fromJson(status.genEntityJsonStr(),Map.class);
        }
        return rs;
    }

    /**
     * 检查唯一
     */
    @RequestMapping(method = RequestMethod.POST,value = "/check",produces = "text/plain")
    @ResponseBody
    public String check(@RequestParam(value = "id",required = false) String id,
                        @RequestParam(value = "code",required = true) String code){
        String unique="true";
        List<GlobalStatus> statuses=this.hibernateHandler.getEntityListByProperty(GlobalStatus.class,"code",code);
        if(id != null){
            GlobalStatus  oldStatus=this.hibernateHandler.getEntityById(GlobalStatus.class, id);
            if(statuses.size() > 0){
                unique = oldStatus.getId().equals(statuses.get(0).getId()) ? "true" : "false";
            }
        }else{
            unique = statuses.size() > 0 ? "false" : "true";
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
        if(idArray!=null && idArray.length!=0){
            StringBuilder sql=new StringBuilder();
            List<String> objectParams=new ArrayList<>();
            sql.append("delete from global_status where id in (");
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
                     @RequestParam(value = "code",required = true) String code,
                     @RequestParam(value = "cateCode",required = false) String cateCode,
                     @RequestParam(value = "showNameEn",required = false) String showNameEn,
                     @RequestParam(value = "showNameCn",required = false) String showNameCn,
                     @RequestParam(value = "unitName",required = false) String unitName,
                     @RequestParam(value = "valType",required = false) String valType,
                     @RequestParam(value = "remark",required = false) String remark){
        GlobalStatus  status = null;
        if(id != null){
            status=this.hibernateHandler.getEntityById(GlobalStatus.class,id);
        }
        if(status == null){
            status=new GlobalStatus();
        }
        status.setCode(code);
        status.setCateCode(cateCode);
        status.setShowNameCn(showNameCn);
        status.setShowNameEn(showNameEn);
        status.setUnitName(unitName);
        status.setValType(valType);
        status.setRemark(remark);
        if(status.getId()!=null){
            this.hibernateHandler.modifyEntity(status);
        }else{
            this.hibernateHandler.addEntity(status);
        }
    }


    /**
     * 查询列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "code",required = false) String code,
                            @RequestParam(value = "cateCode",required = false) String cateCode,
                            @RequestParam(value = "showNameEn",required = false) String showNameEn,
                            @RequestParam(value = "showNameCn",required = false) String showNameCn,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<QueryParam> queryParamList=new ArrayList<>();
        if(!StringUtils.isEmpty(code)){
            queryParamList.add(new QueryParam("and","code","like","%"+code+"%"));
        }
        if(!StringUtils.isEmpty(cateCode)){
            queryParamList.add(new QueryParam("and","cateCode","like","%"+cateCode+"%"));
        }
        if(!StringUtils.isEmpty(showNameEn)){
            queryParamList.add(new QueryParam("and","showNameEn","like","%"+showNameEn+"%"));
        }
        if(!StringUtils.isEmpty(showNameCn)){
            queryParamList.add(new QueryParam("and","showNameCn","like","%"+showNameCn+"%"));
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(GlobalStatus.class, queryParamList));
        List<GlobalStatus> rs=this.hibernateHandler.getEntityListByQueryParam(GlobalStatus.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        for(GlobalStatus status:rs){
            String jsonStr=status.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }

        return queryResult;
    }

    /**
     * 查询车况
     */
    @RequestMapping(method = RequestMethod.POST,value = "/queryStatus")
    @ResponseBody
    public QueryResult queryStatus(@RequestParam(value = "q",required = false) String keywords){
        QueryResult queryResult=new QueryResult();
        queryResult.getOrders().put("code", "asc");
        List<GlobalStatus> rs;
        if(keywords!=null && keywords.length()>0){
            List<QueryParam> queryParamList=new ArrayList<>();
            queryParamList.add(new QueryParam("and (","code","like","%"+keywords+"%"));
            queryParamList.add(new QueryParam("or", "showNameCn", "like", "%"+keywords+"%"));
            queryParamList.add(new QueryParam("or", "showNameEn", "like", "%"+keywords+"%"));
            queryParamList.add(new QueryParam(") and", "id", "is", "not null"));

            rs=this.hibernateHandler.getEntityListByQueryParam(GlobalStatus.class, queryParamList,queryResult.getOrders());
        }else{
            rs=this.hibernateHandler.getEntityListAll(GlobalStatus.class, queryResult.getOrders());
        }
        Gson gson=new Gson();
        for(GlobalStatus status:rs){
            String jsonStr=status.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }
        return queryResult;
    }


}
