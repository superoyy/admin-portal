package com.timanetworks.iov.web.action.monitor;

import com.google.gson.Gson;
import com.timanetworks.iov.domain.VehicleGateway;
import com.timanetworks.iov.domain.VehicleOnline;
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
 * Created by dukla.ou on 2017/8/11.
 */
@Controller
@RequestMapping("/iov/monitor/vehOnline")
public class VehOnlineAction extends GenericAction {

    private static final Logger logger= LoggerFactory.getLogger(VehOnlineAction.class);

    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/monitor/veh_online";
    }

    /**
     * vg树
     */
    @RequestMapping(method = RequestMethod.POST,value = "/showVgTree")
    @ResponseBody
    public QueryResult showVgTree(@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                       @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                                       @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                                       @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<Map<String,Object>> nodeRows=new ArrayList<>();
        //根节点
        Map<String,Object> row=new HashMap<String,Object>(){{
            this.put("id","0");
            this.put("text","车辆网关节点");
            this.put("ip","all");
            this.put("port","all");
            this.put("vehModeCode","all");
        }};
        List<VehicleGateway> vgList=this.hibernateHandler.getEntityListAll(VehicleGateway.class,new HashMap<String,String>(){{
            put("vehicleModeCode","ASC");
        }});

        if(vgList.size()>0){
            row.put("state","open");
        }else{
            row.put("state","closed");
        }
        nodeRows.add(row);

        for(VehicleGateway vg:vgList){
            //vg节点
            row=new HashMap<String,Object>(){{
                int count=Kit.getObjInteger(sqlHandler.getOneValue("select count(id) from vehicle_online where status=0 and vg_ip=? and vg_port=?",new Object[]{vg.getIp(),vg.getPort()}));
                StringBuilder text=new StringBuilder();
                text.append(vg.getVehicleModeCode()).append("(")
                    .append(vg.getIp())
                    .append(":")
                    .append(String.valueOf(vg.getPort()))
                    .append(")-")
                    .append(vg.getStatus()==0 ? "<span style='color:green;'>运行</span>" : "<span style='color:red;'>停机</span>");

                this.put("id",vg.getId());
                this.put("text",text);
                this.put("ip",vg.getIp());
                this.put("port",vg.getPort());
                this.put("vehModeCode",vg.getVehicleModeCode());
                this.put("status",vg.getStatus());

                StringBuilder panelText=new StringBuilder();
                panelText.append(text)
                        .append(",在线:")
                        .append(String.valueOf(count))
                        .append(",心跳:")
                        .append(Kit.formatDateTime(vg.getHeartbeatTime(), "yyyy/MM/dd HH:mm:ss"))
                        .append(",停机:")
                        .append(Kit.formatDateTime(vg.getStopTime(), "yyyy/MM/dd HH:mm:ss"))
                        .append("(")
                        .append( vg.getStopReason()==null?"正常":(vg.getStopReason()==0 ? "正常" : (vg.getStopReason()==1 ? "超时" :"未知")))
                        .append(")");
                this.put("panelText",panelText);

                this.put("_parentId","0");
            }};
            nodeRows.add(row);
        }

        //车型在线统计
        String sql="select c.vehicle_mode_code,count(c.vin) as online_count from (select b.vehicle_mode_code,a.* from vehicle_online a inner join (select vehicle_mode_code,ip,port from vehicle_gateway) b on a.vg_ip=b.ip and a.vg_port=b.port where a.status=0) c group by c.vehicle_mode_code";
        List<Map<String,Object>> recs=this.sqlHandler.getRecordsList(sql);
        List<Map<String,String>> footer=new ArrayList<>();

        int total=0;
        for(Map<String,Object> rec:recs){
            Map<String,String> f=new HashMap<>();
            int count=Kit.getObjInteger(rec.get("ONLINE_COUNT"));
            total +=count;
            String text=Kit.getObjStr(rec.get("VEHICLE_MODE_CODE"))+":"+String.valueOf(count);
            f.put("text",text);
            footer.add(f);
        }

        final String text="在线车辆合计:"+String.valueOf(total);
        footer.add(new HashMap<String,String>(){{
            put("text",text);
        }});

        QueryResult queryResult=new QueryResult();
        queryResult.setTotal(nodeRows.size());
        queryResult.setRows(nodeRows);
        queryResult.setFooter(footer);

        return queryResult;
    }

    /**
     * 在线车辆列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "ip",required = true) String ip,
                            @RequestParam(value = "port",required = true) int port,
                            @RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "onlineTime") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "desc") String order){

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort, order);

        if(vehModeCode != null && !"all".equals(vehModeCode)){
            Map<String,Object> queryParam=new HashMap<String,Object>(){{
                put("vgIp",ip);
                put("vgPort",port);
            }};
            queryResult.setTotal(this.hibernateHandler.getEntityCountByPropertys(VehicleOnline.class, queryParam));
            List<VehicleOnline> rs=this.hibernateHandler.getEntityListByPropertys(VehicleOnline.class, queryParam, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            rs.stream().forEach(vehicleOnline->{
                String jsonStr=vehicleOnline.genEntityJsonStr();
                Map<String,Object> r=gson.fromJson(jsonStr,Map.class);
                r.put("onlineTime",Kit.formatDateTime(vehicleOnline.getOnlineTime(),"yyyy-MM-dd HH:mm:ss"));
                r.put("offlineTime",Kit.formatDateTime(vehicleOnline.getOfflineTime(),"yyyy-MM-dd HH:mm:ss"));
                queryResult.getRows().add(r);
            });
        }
        return queryResult;
    }

    /**
     * 在线车辆列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/search")
    @ResponseBody
    public QueryResult search(@RequestParam(value = "vin",required = true) String vin){

        QueryResult queryResult=new QueryResult();
        List<VehicleOnline> rs=this.hibernateHandler.getEntityListByProperty(VehicleOnline.class,"vin",vin);
        queryResult.setTotal(0);
        if(rs.size()>0){
            Gson gson=new Gson();
            rs.stream().forEach(vehicleOnline->{
                String jsonStr=vehicleOnline.genEntityJsonStr();
                Map<String,Object> r=gson.fromJson(jsonStr,Map.class);
                r.put("onlineTime",Kit.formatDateTime(vehicleOnline.getOnlineTime(),"yyyy-MM-dd HH:mm:ss"));
                r.put("offlineTime",Kit.formatDateTime(vehicleOnline.getOfflineTime(),"yyyy-MM-dd HH:mm:ss"));
                queryResult.getRows().add(r);
            });
            queryResult.setTotal(rs.size());
        }
        return queryResult;
    }


}


