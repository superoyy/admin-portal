package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.GlobalStatus;
import com.timanetworks.iov.domain.VehStatusStruc;
import com.timanetworks.iov.domain.SysCodes;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import com.timanetworks.iov.web.handler.CodesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by dukla.ou on 2016/12/7.
 * 车况结构
 */
@Controller
@RequestMapping("/iov/metadata/vehStatusStruc")
public class VehStatusStrucMngAction extends GenericAction {

    @Autowired
    CodesHandler codesHandler;

    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/veh_status_struc";
    }

    /**
     * 展示
     */
    @RequestMapping(method = RequestMethod.POST,value = "/showVehModeTree")
    @ResponseBody
    public QueryResult showVehModeTree(@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                       @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                                       @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                                       @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<Map<String,Object>> nodeRows=new ArrayList<>();
        //根节点
        Map<String,Object> row=new HashMap<String,Object>(){{
            this.put("id","0");
            this.put("text","车系");
            this.put("vehModeCode","all");
            this.put("protocolVersion","all");
        }};
        List<SysCodes> vehSeriesCodesList=this.codesHandler.getSysCodesByType("VEH_SERIES_MAPPING");
        if(vehSeriesCodesList.size()>0){
            row.put("state","open");
        }else{
            row.put("state","closed");
        }
        nodeRows.add(row);

        for(SysCodes vehSeriesCodes:vehSeriesCodesList){
            if(nodeRows.stream().filter(row1 -> row1.get("vehModeCode").equals(vehSeriesCodes.getCodeValue())).count()>0){
                continue;
            }
            //车型节点
            row=new HashMap<String,Object>(){{
                this.put("id",vehSeriesCodes.getId());
                this.put("text",vehSeriesCodes.getCodeValue());
                this.put("vehModeCode",vehSeriesCodes.getCodeValue());
                this.put("protocolVersion","all");
                this.put("_parentId","0");
            }};
            StringBuilder sql=new StringBuilder();
            sql.append("select protocol_version from veh_status_struc where veh_mode_code = ? and protocol_version is not null group by protocol_version");
            List<Map<String, Object>> recs=this.sqlHandler.getRecordsList(sql.toString(),new Object[]{vehSeriesCodes.getCodeValue()});
            if(recs.size()>0){
                row.put("state","open");
            }else{
                row.put("state","closed");
            }
            nodeRows.add(row);

            //版本节点
            for(Map<String, Object> rec:recs){
                row=new HashMap<String,Object>(){{
                    this.put("id", Kit.get36UUID());
                    this.put("text", "Version: "+Kit.getObjStr(rec.get("PROTOCOL_VERSION")));
                    this.put("vehModeCode",vehSeriesCodes.getCodeValue());
                    this.put("protocolVersion", Kit.getObjStr(rec.get("PROTOCOL_VERSION")));
                    this.put("_parentId",vehSeriesCodes.getId());
                }};
                nodeRows.add(row);
            }
        }

        QueryResult queryResult=new QueryResult();
        queryResult.setTotal(nodeRows.size());
        queryResult.setRows(nodeRows);

        return queryResult;
    }

    /**
     * 车型-版本列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/listVehModeVersion")
    @ResponseBody
    public List<Map<String,Object>> listVehModeVersion(){
        String sql="select CONCAT(veh_mode_code,',',protocol_version) as val,CONCAT(veh_mode_code,' Version: ',protocol_version) as text from veh_status_struc where protocol_version is not null and veh_mode_code is not null group by veh_mode_code,protocol_version";
        List<Map<String,Object>> recs=this.sqlHandler.getRecordsList(sql);
        if(recs!=null && recs.size()>0){
            return recs;
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * 车况列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "vehModeCode",required = false) String vehModeCode,
                            @RequestParam(value = "protocolVersion",required = false) String protocolVersion,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort, order);

        if(protocolVersion != null && vehModeCode != null && !"all".equals(vehModeCode) && !"all".equals(protocolVersion)){
            List<QueryParam> queryParamList=new ArrayList<>();
            queryParamList.add(new QueryParam("and","vehModeCode","=",vehModeCode));
            queryParamList.add(new QueryParam("and", "protocolVersion", "=", protocolVersion));
            queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(VehStatusStruc.class, queryParamList));
            List<VehStatusStruc> rs=this.hibernateHandler.getEntityListByQueryParam(VehStatusStruc.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            for(VehStatusStruc statusStruc:rs){
                String jsonStr=statusStruc.genEntityJsonStr();
                Map r=gson.fromJson(jsonStr,Map.class);
                if(statusStruc.getValConver()!=null && statusStruc.getValConver().length() > 0){
                    r.put("valConver",Base64.getEncoder().encodeToString(statusStruc.getValConver().getBytes()));
                }
                queryResult.getRows().add(r);
            }
        }
        return queryResult;

    }

    /**
     * 删除车系-版本
     */
    @RequestMapping(method = RequestMethod.POST,value = "/removeVehProtocol")
    @ResponseBody
    public void removeVehProtocol(@RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                                  @RequestParam(value = "protocolVersion",required = true) String protocolVersion){

        String sql="delete from veh_status_struc where veh_mode_code=? and protocol_version=?";
        this.sqlHandler.execute(sql,new Object[]{vehModeCode,protocolVersion});
    }


    /**
     * 添加车况结构
     */
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    @ResponseBody
    public void add(@RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                    @RequestParam(value = "protocolVersion",required = true) String protocolVersion){
        VehStatusStruc vehStatusStruc=new VehStatusStruc();
        vehStatusStruc.setVehModeCode(vehModeCode);
        vehStatusStruc.setProtocolVersion(protocolVersion);

        String sql="select max(node_order) as max_order_num from veh_status_struc where veh_mode_code=? and protocol_version=?";
        Object obj=this.sqlHandler.getOneValue(sql,new Object[]{vehModeCode,protocolVersion});
        Integer orderNum = 0;
        if(obj!=null){
            orderNum=Kit.getObjInteger(obj)+1;
        }
        vehStatusStruc.setNodeOrder(orderNum);
        this.hibernateHandler.addEntity(vehStatusStruc);
    }

    /**
     * 添加车系协议版本
     */
    @RequestMapping(method = RequestMethod.POST,value = "/addVehProtocolVersion")
    @ResponseBody
    public Map<String,String> addVehProtocolVersion(@RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                                                    @RequestParam(value = "protocolVersion",required = true) String protocolVersion,
                                                    @RequestParam(value = "copyVehModeCode",required = false) String copyVehModeCode){
        String sql;
        Map<String,String> result=new HashMap<>();
        if(copyVehModeCode!=null && copyVehModeCode.length()>1){
            String[] dd=copyVehModeCode.split(",");
            if(dd.length==2){
                String cpVehModeCode=dd[0];
                String cpProtocolVersion=dd[1];
                if(vehModeCode.equals(cpVehModeCode) && protocolVersion.equals(cpProtocolVersion)){
                    result.put("success","false");
                    result.put("message","不能自我复制!");
                }else{
                    sql="select count(id) from veh_status_struc where veh_mode_code=? and protocol_version=?";
                    Object obj=this.sqlHandler.getOneValue(sql,new Object[]{vehModeCode,protocolVersion});
                    if(Kit.getObjInteger(obj) > 0){
                        result.put("success","false");
                        result.put("message","数据已经存在!");
                    }else{
                        sql="insert into veh_status_struc (id,veh_mode_code,protocol_version,status_id,status_code,node_type,node_order,node_val,up_status_id,up_status_code,did_code,signal_index,val_conver) select uuid(),?,?,status_id,status_code,node_type,node_order,node_val,up_status_id,up_status_code,did_code,signal_index,val_conver from veh_status_struc where veh_mode_code=? and protocol_version=?";
                        int count=this.sqlHandler.execute(sql,new Object[]{vehModeCode,protocolVersion,cpVehModeCode,cpProtocolVersion});
                        result.put("success","true");
                        result.put("message","成功复制:"+count+"条数据!");
                    }
                }
            }else{
                result.put("success","false");
                result.put("message","复制失败!");
            }
        }else{
            sql="insert into veh_status_struc (id,veh_mode_code,protocol_version) values (uuid(),?,?)";
            this.sqlHandler.execute(sql,new Object[]{vehModeCode,protocolVersion});
            result.put("success","true");
            result.put("message","版本号添加成功!");
        }
        return result;
    }

    /**
     * 更新公式
     */
    @RequestMapping(method = RequestMethod.POST,value = "/modifyValConver")
    @ResponseBody
    public void modifyValConver(@RequestParam(value = "id",required = true) String id,
                                   @RequestParam(value = "valConver",required = false) String valConver){
        String sql="update veh_status_struc set val_conver=? where id=?";
        this.sqlHandler.execute(sql,new Object[]{valConver,id});
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
            sql.append("delete from veh_status_struc where id in (");
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
                     @RequestParam(value = "statusId",required = false) String statusId,
                     @RequestParam(value = "nodeType",required = false) String nodeType,
                     @RequestParam(value = "nodeOrder",required = false) String nodeOrder,
                     @RequestParam(value = "nodeVal",required = false) String nodeVal,
                     @RequestParam(value = "upStatusId",required = false) String upStatusId,
                     @RequestParam(value = "didCode",required = false) String didCode,
                     @RequestParam(value = "signalIndex",required = false) String signalIndex){

        VehStatusStruc statusStruc=this.hibernateHandler.getEntityById(VehStatusStruc.class,id);
        if(statusStruc!=null){
            if(statusId!=null){
                GlobalStatus globalStatus=this.hibernateHandler.getEntityById(GlobalStatus.class,statusId);
                if(globalStatus!=null){
                    statusStruc.setGlobalStatus(globalStatus);
                    statusStruc.setStatusCode(globalStatus.getCode());
                }
            }
            if(upStatusId!=null){
                GlobalStatus upGlobalStatus=this.hibernateHandler.getEntityById(GlobalStatus.class,upStatusId);
                if(upGlobalStatus!=null){
                    statusStruc.setUpGlobalStatus(upGlobalStatus);
                    statusStruc.setUpStatusCode(upGlobalStatus.getCode());
                }
            }
            if(Kit.isInt(nodeType)){
                statusStruc.setNodeType(Integer.parseInt(nodeType));
            }
            if(Kit.isInt(nodeOrder)){
                statusStruc.setNodeOrder(Integer.parseInt(nodeOrder));
            }
            if(Kit.isInt(didCode)){
                statusStruc.setDidCode(Integer.parseInt(didCode));
            }
            if(Kit.isInt(signalIndex)){
                statusStruc.setSignalIndex(Integer.parseInt(signalIndex));
            }
            statusStruc.setNodeVal(nodeVal);
            this.hibernateHandler.modifyEntity(statusStruc);
        }
    }


}
