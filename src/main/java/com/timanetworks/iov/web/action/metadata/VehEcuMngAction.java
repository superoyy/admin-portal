package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.domain.CustomEcu;
import com.timanetworks.iov.domain.CustomVehEcu;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 12/2/16.
 */

@Controller
@RequestMapping("/iov/metadata/vehEcu")
public class VehEcuMngAction extends GenericAction {

    @Autowired
    CodesHandler codesHandler;

    /**
	 * 进入
	 */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/veh_ecu";
    }

    /**
     * 车型树
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
        }};
        List<SysCodes> vehSeriesCodesList=this.codesHandler.getSysCodesByType("VEH_SERIES_MAPPING");
        if(vehSeriesCodesList.size()>0){
            row.put("state","open");
        }else{
            row.put("state","closed");
        }
        nodeRows.add(row);

        String sql="select count(id) from custom_veh_ecu where veh_mode_code=?";
        for(SysCodes vehSeriesCodes:vehSeriesCodesList){
            if(nodeRows.stream().filter(row1 -> row1.get("vehModeCode").equals(vehSeriesCodes.getCodeValue())).count()>0){
                continue;
            }
            //车型节点
            row=new HashMap<String,Object>(){{
                int count=Kit.getObjInteger(sqlHandler.getOneValue(sql,new Object[]{vehSeriesCodes.getCodeValue()}));
                this.put("id",vehSeriesCodes.getId());
                this.put("text",vehSeriesCodes.getCodeValue()+"-"+count);
                this.put("vehModeCode",vehSeriesCodes.getCodeValue());
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
     * 车系ECU列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "vehModeCode",required = false) String vehModeCode,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort, order);

        if(vehModeCode != null && !"all".equals(vehModeCode)){
            queryResult.setTotal(this.hibernateHandler.getEntityCountByProperty(CustomVehEcu.class, "vehModeCode", vehModeCode));
            List<CustomVehEcu> rs=this.hibernateHandler.getEntityListByProperty(CustomVehEcu.class, "vehModeCode", vehModeCode, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            rs.stream().forEach(dtc->{
                String jsonStr=dtc.genEntityJsonStr();
                queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
            });
        }
        return queryResult;
    }

    /**
     * 清空车系-ECU
     */
    @RequestMapping(method = RequestMethod.POST,value = "/clear")
    @ResponseBody
    public void clear(@RequestParam(value = "vehModeCode",required = true) String vehModeCode){

        String sql="delete from custom_veh_ecu where veh_mode_code=?";
        this.sqlHandler.execute(sql,new Object[]{vehModeCode});
    }


    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    @ResponseBody
    public void add(@RequestParam(value = "vehModeCode",required = true) String vehModeCode){
        CustomVehEcu vehEcu=new CustomVehEcu();
        vehEcu.setVehModeCode(vehModeCode);
        this.hibernateHandler.addEntity(vehEcu);
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
            sql.append("delete from custom_veh_ecu where id in (");
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
                     @RequestParam(value = "ecuId",required = false) String ecuId,
                     @RequestParam(value = "scoreFlag",required = false) String scoreFlag,
                     @RequestParam(value = "scoreTotal",required = false) String scoreTotal,
                     @RequestParam(value = "remark",required = false) String remark){

        CustomVehEcu vehEcu=this.hibernateHandler.getEntityById(CustomVehEcu.class,id);
        if(vehEcu!=null){
            if(ecuId!=null){
                CustomEcu customEcu=this.hibernateHandler.getEntityById(CustomEcu.class,ecuId);
                if(customEcu!=null){
                    vehEcu.setCustomEcu(customEcu);
                    vehEcu.setEcuCode(customEcu.getCode());
                }
            }
            if(Kit.isInt(scoreFlag)){
                vehEcu.setScoreFlag(Integer.parseInt(scoreFlag));
            }
            if(Kit.isInt(scoreTotal)){
                vehEcu.setScoreTotal(Integer.parseInt(scoreTotal));
            }
            vehEcu.setRemark(remark);
            this.hibernateHandler.modifyEntity(vehEcu);
        }
    }





}
