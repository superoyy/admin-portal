package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.domain.CustomDtc;
import com.timanetworks.iov.domain.CustomEcu;
import com.timanetworks.iov.domain.SysCodes;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.util.excel.ExcelColumn;
import com.timanetworks.iov.util.excel.ExcelConfig;
import com.timanetworks.iov.util.excel.ExcelHandler;
import com.timanetworks.iov.util.excel.ExcelMessage;
import com.timanetworks.iov.web.base.CoreConstant;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import com.timanetworks.iov.web.handler.CodesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by dukla on 12/2/16.
 */

@Controller
@RequestMapping("/iov/metadata/vehDtc")
public class VehDtcMngAction extends GenericAction {

    protected static final Logger logger = LoggerFactory.getLogger(VehDtcMngAction.class);

    @Autowired
    CodesHandler codesHandler;

    /**
	 * 进入
	 */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/veh_dtc";
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

        String sql="select count(id) from custom_dtc where veh_mode_code=?";
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
     * DTC列表
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
            queryResult.setTotal(this.hibernateHandler.getEntityCountByProperty(CustomDtc.class, "vehModeCode", vehModeCode));
            List<CustomDtc> rs=this.hibernateHandler.getEntityListByProperty(CustomDtc.class, "vehModeCode", vehModeCode, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

            Gson gson=new Gson();
            rs.stream().forEach(dtc->{
                String jsonStr=dtc.genEntityJsonStr();
                queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
            });
        }
        return queryResult;
    }

    /**
     * 清空车系-DTC
     */
    @RequestMapping(method = RequestMethod.POST,value = "/clear")
    @ResponseBody
    public void clear(@RequestParam(value = "vehModeCode",required = true) String vehModeCode){

        String sql="delete from custom_dtc where veh_mode_code=?";
        this.sqlHandler.execute(sql,new Object[]{vehModeCode});
    }


    /**
     * 添加
     */
    @RequestMapping(method = RequestMethod.POST,value = "/add")
    @ResponseBody
    public void add(@RequestParam(value = "vehModeCode",required = true) String vehModeCode){
        CustomDtc dtc=new CustomDtc();
        dtc.setVehModeCode(vehModeCode);
        this.hibernateHandler.addEntity(dtc);
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
            sql.append("delete from custom_dtc where id in (");
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
                     @RequestParam(value = "code",required = false) String code,
                     @RequestParam(value = "ecuId",required = false) String ecuId,
                     @RequestParam(value = "level",required = false) String level,
                     @RequestParam(value = "failureType",required = false) String failureType,
                     @RequestParam(value = "dispFlag",required = false) String dispFlag,
                     @RequestParam(value = "describCh",required = false) String describCh,
                     @RequestParam(value = "describEn",required = false) String describEn,
                     @RequestParam(value = "overruledFlag",required = false) String overruledFlag){

        CustomDtc dtc=this.hibernateHandler.getEntityById(CustomDtc.class,id);
        if(dtc!=null){
            if(ecuId!=null){
                CustomEcu customEcu=this.hibernateHandler.getEntityById(CustomEcu.class,ecuId);
                if(customEcu!=null){
                    dtc.setCustomEcu(customEcu);
                    dtc.setEcuCode(customEcu.getCode());
                }
            }
            dtc.setCode(code);
            if(Kit.isInt(level)){
                dtc.setLevel(Integer.parseInt(level));
            }
            dtc.setFailureType(failureType);
            if(Kit.isInt(dispFlag)){
                dtc.setDispFlag(Integer.parseInt(dispFlag));
            }
            dtc.setDescribCh(describCh);
            dtc.setDescribEn(describEn);
            if(Kit.isInt(overruledFlag)){
                dtc.setOverruledFlag(Integer.parseInt(overruledFlag));
            }
            this.hibernateHandler.modifyEntity(dtc);
        }
    }

    private ExcelHandler getExcelHandler(){
        //列定义
        List<ExcelColumn> excelColumnList=new ArrayList<ExcelColumn>();
        ExcelColumn column=new ExcelColumn(0,"ecuCode", ExcelColumn.TYPE_NUMBER,0,false,"\\d+","请输入0-99整数");
        excelColumnList.add(column);

        column=new ExcelColumn(1,"dtcCode", ExcelColumn.TYPE_STRING,20,false,null,"请输入20位以内ASCII字符");
        excelColumnList.add(column);

        column=new ExcelColumn(2,"level", ExcelColumn.TYPE_NUMBER,0,false,null,"请输入1|2|3");
        excelColumnList.add(column);

        column=new ExcelColumn(3,"describe", ExcelColumn.TYPE_STRING,100,true,null,"小于100个字符");
        excelColumnList.add(column);

        //配置处理器
        ExcelConfig config=new ExcelConfig();
        config.setOfficeVer(ExcelConfig.OFFICE_2007);
        config.setExcelColumnList(excelColumnList);
        config.setStartRowNum(1);
        config.setFileSize(1024000L);
        config.setVerifyFilePath(CoreConstant.SYS_PROP.get(CoreConstant.CONTEXT_REAL_PATH));
        config.setVerifyFileName("dtc_import_verify_"+Kit.formatDateTime(new Date(),"YYYYMMddHHmmss")+".xlsx");
        return new ExcelHandler(config);
    }

    /**
     * 导入
     */
    @RequestMapping(method = RequestMethod.POST,value = "/import")
    @ResponseBody
    public Map<String,String> importDtc(@RequestParam(value = "vehModeCode",required = true) String vehModeCode,
                                        @RequestParam(value = "dtcFile",required = true) MultipartFile dtcFile){
        Map<String,String> result=new HashMap<>();
        boolean success=true;
        if(!dtcFile.isEmpty()){
            try {
                logger.info("import:{}->{}",vehModeCode,dtcFile.getOriginalFilename());
                List<Map<String,Object>> recs=new ArrayList<>();
                ExcelHandler excelHandler=this.getExcelHandler();
                List<ExcelMessage> readMsgs = excelHandler.readExcel(dtcFile.getOriginalFilename(), dtcFile.getSize(), dtcFile.getInputStream(), recs, true);
                for(ExcelMessage excelMessage:readMsgs){
                    if(excelMessage.getLevel() == ExcelMessage.LEVEL_ERROR){
                        logger.info("Verify Dtc File error:level={},message={}",excelMessage.getLevel(),excelMessage.getMessage());
                        success=false;
                        break;
                    }
                }
                if(success){
                    StringBuilder sql=new StringBuilder("delete from custom_dtc where veh_mode_code=?");
                    this.sqlHandler.execute(sql.toString(),new Object[]{vehModeCode});
                    for(Map<String,Object> rec:recs){
                        sql.delete(0,sql.length());
                        sql.append("select id from custom_ecu where code=?");
                        String ecuCode=String.valueOf(Kit.getObjDouble(rec.get("ecuCode")).intValue());
                        String ecuId=Kit.getObjStr(this.sqlHandler.getOneValue(sql.toString(),new Object[]{ecuCode}));
                        int level=Kit.getObjDouble(rec.get("level")).intValue();
                        sql.delete(0,sql.length());
                        sql.append("insert into custom_dtc (id,veh_mode_code,ecu_id,ecu_code,code,level,describ_ch,describ_en) values (?,?,?,?,?,?,?,?)");
                        this.sqlHandler.execute(sql.toString(),new Object[]{Kit.get36UUID(),vehModeCode,ecuId,ecuCode,rec.get("dtcCode"),level,rec.get("describe"),rec.get("describe")});
                    }
                    result.put("success","true");
                    result.put("message","导入成功!");
                }else{
                    result.put("success","false");
                    result.put("message","导入失败!<a href='/"+excelHandler.getExcelConfig().getVerifyFileName()+"'>下载</a>");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                result.put("success","false");
                result.put("message", "文件读取失败!");
            }
        }else{
            result.put("success","false");
            result.put("message","文件为空！");
        }

        return result;
    }






}
