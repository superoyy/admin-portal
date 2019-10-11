package com.timanetworks.iov.web.action.monitor;

import com.google.gson.Gson;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
@RequestMapping("/iov/monitor/vehStatusSnapshot")
public class VehStatusSnapshotAction extends GenericAction {

    private static final Logger logger= LoggerFactory.getLogger(VehStatusSnapshotAction.class);


    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/monitor/veh_status_snapshot";
    }

    /**
     * 查询
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "vin",required = false) String vin,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "vin") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);
        List<Map<String,Object>> recs=new ArrayList<>();

        Query query=new Query();
        query.limit(queryResult.getCount());
        query.skip(queryResult.getStart());
        if ("ASC".equals(order.toUpperCase())) {
            Sort st=new Sort(Sort.Direction.ASC,sort);
            query.with(st);
        } else if ("DESC".equals(order.toUpperCase())) {
            Sort st=new Sort(Sort.Direction.DESC,sort);
            query.with(st);
        }
        if(vin!=null && vin.length() > 0){
            query.addCriteria(Criteria.where("vin").is(vin));
        }else{
            query.addCriteria(Criteria.where("vin").ne("").ne(null));
        }

        Long total=this.mongoTemplate.count(query,"vehicle_status_snapshot");

        this.mongoTemplate.executeQuery(query,"vehicle_status_snapshot", dbObject -> {
            Map<String,Object> rawData=dbObject.toMap();
            Map<String,Object> rec=new HashMap<>();
            Gson gson=new Gson();
            if(rawData.containsKey("_id") && rawData.get("_id")!=null){
                rec.put("id",rawData.get("_id"));
                rawData.remove("_id");
            }else{
                rec.put("id","");
            }
            if(rawData.containsKey("vin") && rawData.get("vin")!=null){
                rec.put("vin",rawData.get("vin"));
                rawData.remove("vin");
            }else{
                rec.put("vin","");
            }
            if(rawData.containsKey("iccid") && rawData.get("iccid")!=null){
                rec.put("iccid",rawData.get("iccid"));
                rawData.remove("iccid");
            }else{
                rec.put("iccid","");
            }
            if(rawData.containsKey("imei") && rawData.get("imei")!=null){
                rec.put("imei",rawData.get("imei"));
                rawData.remove("imei");
            }else{
                rec.put("imei","");
            }
            if(rawData.containsKey("veh_series") && rawData.get("veh_series")!=null){
                rec.put("veh_series",rawData.get("veh_series"));
                rawData.remove("veh_series");
            }else{
                rec.put("veh_series","");
            }
            if(rawData.containsKey("update_ts") && rawData.get("update_ts")!=null && (rawData.get("update_ts") instanceof Long)){
                long ts=(Long)rawData.get("update_ts");
                rec.put("update_ts", Kit.formatDateTime(new Date(ts),"yyyy-MM-dd HH:mm:ss"));
            }else{
                rec.put("update_ts","");
            }
            if(rawData.containsKey("isThresholdAlarm") && rawData.get("isThresholdAlarm")!=null){
                rec.put("isThresholdAlarm",rawData.get("isThresholdAlarm"));
                rawData.remove("isThresholdAlarm");
            }else{
                rec.put("isThresholdAlarm","");
            }
            if(rawData.containsKey("isDiagnosisAlarm") && rawData.get("isDiagnosisAlarm")!=null){
                rec.put("isDiagnosisAlarm",rawData.get("isDiagnosisAlarm"));
                rawData.remove("isDiagnosisAlarm");
            }else{
                rec.put("isDiagnosisAlarm","");
            }
            if(rawData.containsKey("isOnline") && rawData.get("isOnline")!=null){
                rec.put("isOnline",rawData.get("isOnline"));
                rawData.remove("isOnline");
            }else{
                rec.put("isOnline","");
            }
            if(rawData.containsKey("isDtcAlarm") && rawData.get("isDtcAlarm")!=null){
                rec.put("isDtcAlarm",rawData.get("isDtcAlarm"));
                rawData.remove("isDtcAlarm");
            }else{
                rec.put("isDtcAlarm","");
            }
            if(rawData.containsKey("DTC") && rawData.get("DTC")!=null && (rawData.get("DTC") instanceof Map)){
                Map dtc=(Map)rawData.get("DTC");
                dtc.remove("_class");
                rec.put("DTC",Base64.getEncoder().encodeToString(gson.toJson(dtc).getBytes()));
                rawData.remove("DTC");
            }else{
                rec.put("DTC","");
            }
            if(rawData.containsKey("DRIVE") && rawData.get("DRIVE")!=null && (rawData.get("DRIVE") instanceof Map)){
                Map drive=(Map)rawData.get("DRIVE");
                drive.remove("_class");
                rec.put("DRIVE",Base64.getEncoder().encodeToString(gson.toJson(drive).getBytes()));
                rawData.remove("DRIVE");
            }else{
                rec.put("DRIVE","");
            }

            StringBuilder sb=new StringBuilder();
            for(String key:rawData.keySet()){
                Object obj=rawData.get(key);
                if(obj!=null && obj instanceof Map){
                    Map<String,Object> status=(Map) obj;
                    status.remove("_class");
                    if(status.containsKey("recv_ts") && status.get("recv_ts")!=null && status.get("recv_ts") instanceof Long){
                        long ts=(Long)status.get("recv_ts");
                        status.put("recv_ts", Kit.formatDateTime(new Date(ts),"yyyy-MM-dd HH:mm:ss"));
                    }
                    if(status.containsKey("pkg_ts") && status.get("pkg_ts")!=null && status.get("pkg_ts") instanceof Long){
                        long ts=(Long)status.get("pkg_ts");
                        status.put("pkg_ts", Kit.formatDateTime(new Date(ts),"yyyy-MM-dd HH:mm:ss"));
                    }
                    sb.append("\"").append(key).append("\":").append(gson.toJson(status)).append("\n\n");
                }
            }
            rec.put("status_snapshot",Base64.getEncoder().encodeToString(sb.toString().getBytes()));
            recs.add(rec);
            //logger.info("{}", dbObject.toString());
        });
        queryResult.setTotal(total.intValue());
        queryResult.setRows(recs);
        return queryResult;
    }

}


