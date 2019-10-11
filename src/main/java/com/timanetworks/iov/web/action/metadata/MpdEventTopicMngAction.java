package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.domain.MpdEventTopic;
import com.timanetworks.iov.domain.MpdTopic;
import com.timanetworks.iov.domain.GlobalEvent;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by dukla on 12/2/16.
 */

@Controller
@RequestMapping("/iov/metadata/mpdEventTopic")
public class MpdEventTopicMngAction extends GenericAction {

    protected static final Logger logger = LoggerFactory.getLogger(MpdEventTopicMngAction.class);

    /**
	 * 进入
	 */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/mpd_event_topic";
    }

    /**
     * 事件树
     */
    @RequestMapping(method = RequestMethod.POST,value = "/showEventTree")
    @ResponseBody
    public QueryResult showEventTree(@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                       @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                                       @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                                       @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<Map<String,Object>> nodeRows=new ArrayList<>();
        //根节点
        Map<String,Object> row=new HashMap<String,Object>(){{
            this.put("id","0");
            this.put("text","车辆事件");
            this.put("eventCode","all");
        }};
        List<GlobalEvent> globalEvents=this.hibernateHandler.getEntityListAll(GlobalEvent.class, new HashMap<String, String>() {{
            put("code", "asc");
        }});
        if(globalEvents.size()>0){
            row.put("state","open");
        }else{
            row.put("state","closed");
        }
        nodeRows.add(row);

        String sql="select count(id) from mpd_event_topic where event_code=?";
        for(GlobalEvent globalEvent:globalEvents){
            //事件节点
            row=new HashMap<String,Object>(){{
                int count=Kit.getObjInteger(sqlHandler.getOneValue(sql,new Object[]{globalEvent.getCode()}));
                this.put("id",globalEvent.getId());
                this.put("text",globalEvent.getCode()+"-"+count);
                this.put("eventCode",globalEvent.getCode());
                this.put("_parentId","0");
            }};
            nodeRows.add(row);
        }

        QueryResult queryResult=new QueryResult();
        queryResult.setTotal(nodeRows.size());
        queryResult.setRows(nodeRows);

        return queryResult;
    }

    /**
     * 事件分发列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "eventCode",required = false) String eventCode,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort, order);

        if(eventCode != null && !"all".equals(eventCode)){
            queryResult.setTotal(this.hibernateHandler.getEntityCountByProperty(MpdEventTopic.class, "eventCode", eventCode));
            List<MpdEventTopic> rs=this.hibernateHandler.getEntityListByProperty(MpdEventTopic.class, "eventCode", eventCode, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            rs.stream().forEach(eventTopic->{
                String jsonStr=eventTopic.genEntityJsonStr();
                Map r=gson.fromJson(jsonStr,Map.class);
                if(eventTopic.getSignalFilter()!=null && eventTopic.getSignalFilter().length() > 0){
                    r.put("signalFilter",Base64.getEncoder().encodeToString(eventTopic.getSignalFilter().getBytes()));
                }
                if(eventTopic.getAlias()!=null && eventTopic.getAlias().length() > 0){
                    r.put("alias",Base64.getEncoder().encodeToString(eventTopic.getAlias().getBytes()));
                }
                queryResult.getRows().add(r);
            });
        }
        return queryResult;
    }

    /**
     * 清空事件分发队列
     */
    @RequestMapping(method = RequestMethod.POST,value = "/clear")
    @ResponseBody
    public void clear(@RequestParam(value = "eventCode",required = true) String eventCode){

        String sql="delete from mpd_event_topic where event_code=?";
        this.sqlHandler.execute(sql,new Object[]{eventCode});
    }


    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    @ResponseBody
    public void add(@RequestParam(value = "eventCode",required = true) String eventCode){
        MpdEventTopic eventTopic=new MpdEventTopic();
        eventTopic.setEventCode(eventCode);
        this.hibernateHandler.addEntity(eventTopic);
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
            sql.append("delete from mpd_event_topic where id in (");
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
    public void save(@RequestParam(value = "id",required = true) String id,
                     @RequestParam(value = "mpdTopicId",required = false) String mpdTopicId){

        MpdEventTopic eventTopic=this.hibernateHandler.getEntityById(MpdEventTopic.class,id);
        if(eventTopic!=null){
            if(mpdTopicId!=null){
                MpdTopic mpdTopic=this.hibernateHandler.getEntityById(MpdTopic.class,mpdTopicId);
                if(mpdTopic!=null){
                    eventTopic.setMpdTopic(mpdTopic);
                }
            }
            this.hibernateHandler.modifyEntity(eventTopic);
        }
    }

    /**
     * 更新过滤信号
     */
    @RequestMapping(method = RequestMethod.POST,value = "/modifyFilter")
    @ResponseBody
    public void modifyRemark(@RequestParam(value = "id",required = true) String id,
                             @RequestParam(value = "valFilter",required = false) String valFilter){
        String sql="update mpd_event_topic set signal_filter=? where id=?";
        this.sqlHandler.execute(sql,new Object[]{valFilter,id});
    }

    /**
     * 更新过滤信号
     */
    @RequestMapping(method = RequestMethod.POST,value = "/modifyAlias")
    @ResponseBody
    public void modifyAlias(@RequestParam(value = "id",required = true) String id,
                             @RequestParam(value = "valAlias",required = false) String valAlias){
        String sql="update mpd_event_topic set alias=? where id=?";
        this.sqlHandler.execute(sql,new Object[]{valAlias,id});
    }


}
