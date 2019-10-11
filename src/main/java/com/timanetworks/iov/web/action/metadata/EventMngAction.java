package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.GlobalEvent;
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
@RequestMapping("/iov/metadata/event")
public class EventMngAction extends GenericAction {

    /**
     * 进入模块管理
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/event_list";
    }

    /**
     * 获取
     */
    @RequestMapping(method = RequestMethod.POST,value = "/get")
    @ResponseBody
    public Map<String,Object> get(@RequestParam(value = "id",required = true) String id){
        GlobalEvent event=this.hibernateHandler.getEntityById(GlobalEvent.class,id);
        Map<String,Object> rs=new HashMap<>();
        Gson gson=new Gson();
        if(event!=null){
            rs=gson.fromJson(event.genEntityJsonStr(),Map.class);
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
        List<GlobalEvent> events=this.hibernateHandler.getEntityListByProperty(GlobalEvent.class,"code",code);
        if(id != null){
            GlobalEvent  oldEvent=this.hibernateHandler.getEntityById(GlobalEvent.class, id);
            if(events.size() > 0){
                unique = oldEvent.getId().equals(events.get(0).getId()) ? "true" : "false";
            }
        }else{
            unique = events.size() > 0 ? "false" : "true";
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
            sql.append("delete from global_event where id in (");
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
                     @RequestParam(value = "name",required = false) String name,
                     @RequestParam(value = "grade",required = false) String grade,
                     @RequestParam(value = "remark",required = false) String remark){
        GlobalEvent  event = null;
        if(id != null){
            event=this.hibernateHandler.getEntityById(GlobalEvent.class,id);
        }
        if(event == null){
            event=new GlobalEvent();
        }
        event.setCode(code);
        event.setName(name);
        event.setRemark(remark);
        if(Kit.isInt(grade)){
            event.setGrade(Integer.parseInt(grade));
        }else{
            event.setGrade(0);
        }
        if(event.getId()!=null){
            this.hibernateHandler.modifyEntity(event);
        }else{
            this.hibernateHandler.addEntity(event);
        }
    }


    /**
     * 事件列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "code",required = false) String code,
                     @RequestParam(value = "name",required = false) String name,
                     @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                     @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                     @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                     @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<QueryParam> queryParamList=new ArrayList<>();
        if(code!=null && code.length()!=0){
            queryParamList.add(new QueryParam("and","code","like","%"+code+"%"));
        }
        if(name!=null && name.length()!=0){
            queryParamList.add(new QueryParam("and","name","like","%"+name+"%"));
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(GlobalEvent.class, queryParamList));
        List<GlobalEvent> rs=this.hibernateHandler.getEntityListByQueryParam(GlobalEvent.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        for(GlobalEvent event:rs){
            String jsonStr=event.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }

        return queryResult;
    }

    /**
     * 查询事件
     */
    @RequestMapping(method = RequestMethod.POST,value = "/queryEvent")
    @ResponseBody
    public QueryResult querySignal(@RequestParam(value = "q",required = false) String keywords){
        QueryResult queryResult=new QueryResult();
        queryResult.getOrders().put("code", "asc");
        List<GlobalEvent> rs;
        if(keywords!=null && keywords.length()>0){
            List<QueryParam> queryParamList=new ArrayList<>();
            queryParamList.add(new QueryParam("and (","code","like","%"+keywords+"%"));
            queryParamList.add(new QueryParam("or", "name", "like", "%"+keywords+"%"));
            queryParamList.add(new QueryParam(") and", "id", "is", "not null"));

            rs=this.hibernateHandler.getEntityListByQueryParam(GlobalEvent.class, queryParamList,queryResult.getOrders());
        }else{
            rs=this.hibernateHandler.getEntityListAll(GlobalEvent.class, queryResult.getOrders());
        }
        Gson gson=new Gson();
        for(GlobalEvent event:rs){
            String jsonStr=event.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }
        return queryResult;
    }


}
