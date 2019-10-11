package com.timanetworks.iov.web.action.monitor;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.RemoteOperationCmdHistory;
import com.timanetworks.iov.domain.DialerHistory;
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
 * Created by dukla.ou on 2017/6/7.
 */
@Controller
@RequestMapping("/iov/monitor/remoteOperation")
public class RemoteOperationAction extends GenericAction {

    private static final Logger logger= LoggerFactory.getLogger(RemoteOperationAction.class);

    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/monitor/remote_operation_list";
    }

    /**
     * 查询
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "vin",required = false) String vin,
                            @RequestParam(value = "buildStartTime",required = false) String buildStartTime,
                            @RequestParam(value = "buildEndTime",required = false) String buildEndTime,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "buildTime") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "desc") String order){
        List<QueryParam> queryParamList=new ArrayList<>();
        if(vin!=null && vin.length()!=0){
            queryParamList.add(new QueryParam("and","vin","like","%"+vin+"%"));
        }
        if(buildStartTime!=null && buildStartTime.length()!=0){
            Date st=Kit.parseDateTimeStr(buildStartTime,"yyyy-MM-dd HH:mm:ss");
            if(st!=null){
                queryParamList.add(new QueryParam("and","buildTime",">",st));
            }
        }
        if(buildEndTime!=null && buildEndTime.length()!=0){
            Date et=Kit.parseDateTimeStr(buildEndTime,"yyyy-MM-dd HH:mm:ss");
            if(et!=null){
                queryParamList.add(new QueryParam("and","buildTime","<",et));
            }
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(RemoteOperationCmdHistory.class, queryParamList));
        List<RemoteOperationCmdHistory> rs=this.hibernateHandler.getEntityListByQueryParam(RemoteOperationCmdHistory.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        for(RemoteOperationCmdHistory r:rs){
            String jsonStr=r.genEntityJsonStr();
            Map rec=gson.fromJson(jsonStr, Map.class);
            rec.put("buildTime",Kit.formatDateTime(r.getBuildTime(),"yyyy-MM-dd HH:mm:ss"));
            rec.put("statusUpdateTime",Kit.formatDateTime(r.getStatusUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
            if(r.getParamObj()!=null && r.getParamObj().length() > 0){
                rec.put("paramObj", Base64.getEncoder().encodeToString(r.getParamObj().getBytes()));
            }
            if(r.getResponseContent()!=null && r.getResponseContent().length() > 0){
                rec.put("responseContent", Base64.getEncoder().encodeToString(r.getResponseContent().getBytes()));
            }
            queryResult.getRows().add(rec);
        }

        return queryResult;

    }

    /**
     * 查询
     */
    @RequestMapping(method = RequestMethod.POST,value = "/listRingRecs")
    @ResponseBody
    public QueryResult listRingRecs(@RequestParam(value = "cmdId",required = false) String cmdId,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "buildTime") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "desc") String order){
        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);
        if(cmdId!=null && cmdId.length()!=0){
            queryResult.setTotal(this.hibernateHandler.getEntityCountByProperty(DialerHistory.class, "cmdId",cmdId));
            List<DialerHistory> rs=this.hibernateHandler.getEntityListByProperty(DialerHistory.class, "cmdId",cmdId, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            for(DialerHistory r:rs){
                String jsonStr=r.genEntityJsonStr();
                Map rec=gson.fromJson(jsonStr, Map.class);
                rec.put("buildTime",Kit.formatDateTime(r.getBuildTime(),"yyyy-MM-dd HH:mm:ss"));
                rec.put("finishTime",Kit.formatDateTime(r.getFinishTime(),"yyyy-MM-dd HH:mm:ss"));
                queryResult.getRows().add(rec);
            }
        }else{
            queryResult.setTotal(0);
        }
        return queryResult;

    }

}


