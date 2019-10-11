package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.VehDataConvert;
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
 * Created by dukla on 5/9/17.
 */
@Controller
@RequestMapping("/iov/metadata/vehDataConvert")
public class VehDataConvertMngAction extends GenericAction {

    @Autowired
    CodesHandler codesHandler;

    /**
     * 进入
     */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/veh_data_convert";
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
            sql.append("select protocol_version from veh_data_convert where veh_mode_code = ? and protocol_version is not null group by protocol_version");
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
                    this.put("id",Kit.get36UUID());
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
        String sql="select CONCAT(veh_mode_code,',',protocol_version) as val,CONCAT(veh_mode_code,' Version: ',protocol_version) as text from veh_data_convert where protocol_version is not null and veh_mode_code is not null group by veh_mode_code,protocol_version";
        List<Map<String,Object>> recs=this.sqlHandler.getRecordsList(sql);
        if(recs!=null && recs.size()>0){
            return recs;
        }else{
            return new ArrayList<>();
        }
    }


    /**
     * 列表
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
            queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(VehDataConvert.class, queryParamList));
            List<VehDataConvert> rs=this.hibernateHandler.getEntityListByQueryParam(VehDataConvert.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            for(VehDataConvert dataConvert:rs){
                String jsonStr=dataConvert.genEntityJsonStr();
                Map r=gson.fromJson(jsonStr,Map.class);
                if(dataConvert.getDataConvert()!=null && dataConvert.getDataConvert().length()>0){
                    r.put("dataConvert", Base64.getEncoder().encodeToString(dataConvert.getDataConvert().getBytes()));
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

        String sql="delete from veh_data_convert where veh_mode_code=? and protocol_version=?";
        this.sqlHandler.execute(sql,new Object[]{vehModeCode,protocolVersion});
    }


    /**
     * 添加转换
     */
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    @ResponseBody
    public void add(@RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                    @RequestParam(value = "protocolVersion",required = true) String protocolVersion){
        VehDataConvert dataConvert=new VehDataConvert();
        dataConvert.setVehModeCode(vehModeCode);
        dataConvert.setProtocolVersion(protocolVersion);
        this.hibernateHandler.addEntity(dataConvert);
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
                  sql="select count(id) from veh_data_convert where veh_mode_code=? and protocol_version=?";
                  Object obj=this.sqlHandler.getOneValue(sql,new Object[]{vehModeCode,protocolVersion});
                  if(Kit.getObjInteger(obj) > 0){
                      result.put("success","false");
                      result.put("message","数据已经存在!");
                  }else{
                      sql="insert into veh_data_convert (id,veh_mode_code,protocol_version,data_convert,convert_type) select uuid(),?,?,data_convert,convert_type from veh_data_convert where veh_mode_code=? and protocol_version=?";
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
            sql="insert into veh_data_convert (id,veh_mode_code,protocol_version) values (uuid(),?,?)";
            this.sqlHandler.execute(sql,new Object[]{vehModeCode,protocolVersion});
            result.put("success","true");
            result.put("message","版本号添加成功!");
        }
        return result;
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
            sql.append("delete from veh_data_convert where id in (");
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
     * 更新公式
     */
    @RequestMapping(method = RequestMethod.POST,value = "/modifyConvert")
    @ResponseBody
    public void modifySignalConver(@RequestParam(value = "id",required = true) String id,
                                   @RequestParam(value = "dataConvert",required = false) String dataConvert){
        String sql="update veh_data_convert set data_convert=? where id=?";
        this.sqlHandler.execute(sql,new Object[]{dataConvert,id});
    }

    /**
     * 保存
     */
    @RequestMapping(method = RequestMethod.POST,value = "/save")
    @ResponseBody
    public void save(@RequestParam(value = "id",required = true) String id,
                     @RequestParam(value = "convertType",required = false) String convertType){

        VehDataConvert vehDataConvert=this.hibernateHandler.getEntityById(VehDataConvert.class,id);
        if(vehDataConvert!=null){
            if(Kit.isInt(convertType)){
                vehDataConvert.setConvertType(Integer.parseInt(convertType));
            }
            this.hibernateHandler.modifyEntity(vehDataConvert);
        }
    }



}
