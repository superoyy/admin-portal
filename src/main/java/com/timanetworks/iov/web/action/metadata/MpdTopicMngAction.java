package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.MpdTopic;
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
@RequestMapping("/iov/metadata/mpdTopic")
public class MpdTopicMngAction extends GenericAction {

    /**
     * 进入模块管理
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/mpd_topic_list";
    }

    /**
     * 获取
     */
    @RequestMapping(method = RequestMethod.POST,value = "/get")
    @ResponseBody
    public Map<String,Object> get(@RequestParam(value = "id",required = true) String id){
        MpdTopic mpdTopic=this.hibernateHandler.getEntityById(MpdTopic.class,id);
        Map<String,Object> rs=new HashMap<>();
        Gson gson=new Gson();
        if(mpdTopic!=null){
            rs=gson.fromJson(mpdTopic.genEntityJsonStr(),Map.class);
        }
        return rs;
    }

    /**
     * 检查唯一
     */
    @RequestMapping(method = RequestMethod.POST,value = "/check",produces = "text/plain")
    @ResponseBody
    public String check(@RequestParam(value = "id",required = false) String id,
                        @RequestParam(value = "topicName",required = true) String topicName){
        String unique="true";
        List<MpdTopic> mpdTopics=this.hibernateHandler.getEntityListByProperty(MpdTopic.class,"topicName",topicName);
        if(id != null){
            MpdTopic  oldMpdTopic=this.hibernateHandler.getEntityById(MpdTopic.class, id);
            if(mpdTopics.size() > 0){
                unique = oldMpdTopic.getId().equals(mpdTopics.get(0).getId()) ? "true" : "false";
            }
        }else{
            unique = mpdTopics.size() > 0 ? "false" : "true";
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
            sql.append("delete from mpd_topic where id in (");
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
                     @RequestParam(value = "topicName",required = true) String topicName,
                     @RequestParam(value = "status",required = false) int status){
        MpdTopic  mpdTopic = null;
        if(id != null){
            mpdTopic=this.hibernateHandler.getEntityById(MpdTopic.class,id);
        }
        if(mpdTopic == null){
            mpdTopic=new MpdTopic();
        }
        mpdTopic.setTopicName(topicName);
        mpdTopic.setStatus(status);
        if(mpdTopic.getId()!=null){
            this.hibernateHandler.modifyEntity(mpdTopic);
        }else{
            this.hibernateHandler.addEntity(mpdTopic);
        }
    }


    /**
     * 分发队列查询
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "topicName",required = false) String topicName,
                     @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                     @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                     @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                     @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<QueryParam> queryParamList=new ArrayList<>();
        if(topicName!=null && topicName.length()!=0){
            queryParamList.add(new QueryParam("and","topicName","like","%"+topicName+"%"));
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(MpdTopic.class, queryParamList));
        List<MpdTopic> rs=this.hibernateHandler.getEntityListByQueryParam(MpdTopic.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        String sql="select event_code from mpd_event_topic where mpd_topic_id=? order by event_code";
        for(MpdTopic mpdTopic:rs){
            String jsonStr=mpdTopic.genEntityJsonStr();
            Map recs=gson.fromJson(jsonStr, Map.class);
            List<Map<String, Object>> events=this.sqlHandler.getRecordsList(sql,new Object[]{mpdTopic.getId()});
            StringBuilder sb=new StringBuilder();
            for(Map<String, Object> event:events){
                sb.append(Kit.getObjStr(event.get("EVENT_CODE"))).append("<br/>");
            }
            sb.append("Total = "+events.size());
            recs.put("eventList",sb.toString());
            queryResult.getRows().add(recs);
        }

        return queryResult;
    }

    /**
     * 查询队列
     */
    @RequestMapping(method = RequestMethod.POST,value = "/queryTopic")
    @ResponseBody
    public QueryResult queryTopic(@RequestParam(value = "q",required = false) String keywords){
        QueryResult queryResult=new QueryResult();
        queryResult.getOrders().put("topicName", "asc");
        List<MpdTopic> rs;
        if(keywords!=null && keywords.length()>0){
            List<QueryParam> queryParamList=new ArrayList<>();
            queryParamList.add(new QueryParam("and","topicName","like","%"+keywords+"%"));

            rs=this.hibernateHandler.getEntityListByQueryParam(MpdTopic.class, queryParamList,queryResult.getOrders());
        }else{
            rs=this.hibernateHandler.getEntityListAll(MpdTopic.class, queryResult.getOrders());
        }
        Gson gson=new Gson();
        for(MpdTopic mpdTopic:rs){
            String jsonStr=mpdTopic.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }
        return queryResult;
    }


}
