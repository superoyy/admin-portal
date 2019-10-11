package com.timanetworks.iov.web.action.metadata;

import com.google.gson.Gson;
import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.domain.CustomEcu;
import com.timanetworks.iov.web.base.GenericAction;
import com.timanetworks.iov.web.base.QueryResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 12/2/16.
 */

@Controller
@RequestMapping("/iov/metadata/ecu")
public class EcuMngAction extends GenericAction {

    /**
	 * 进入
	 */
    @RequestMapping(method = RequestMethod.GET,value = "/entry")
    public String entry(){
        return "/iov/metadata/ecu_list";
    }

    /**
	 * 获取
	 */
    @RequestMapping(method = RequestMethod.POST,value = "/get")
    @ResponseBody
    public Map<String,String> get(@RequestParam(value = "id",required = true) String id){
        CustomEcu  ecu=this.hibernateHandler.getEntityById(CustomEcu.class,id);
        Map<String,String> rs=new HashMap<>();
        Gson gson=new Gson();
        if(ecu!=null){
            rs=gson.fromJson(ecu.genEntityJsonStr(),Map.class);
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
        List<CustomEcu> ecus=this.hibernateHandler.getEntityListByProperty(CustomEcu.class,"code",code);
        if(id != null){
            CustomEcu  oldEcu=this.hibernateHandler.getEntityById(CustomEcu.class, id);
            if(ecus.size() > 0){
                unique = oldEcu.getId().equals(ecus.get(0).getId()) ? "true" : "false";
            }
        }else{
            unique = ecus.size() > 0 ? "false" : "true";
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
            sql.append("delete from custom_ecu where id in (");
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
                        @RequestParam(value = "showNameEn",required = true) String showNameEn,
                        @RequestParam(value = "showNameCn",required = false) String showNameCn,
                        @RequestParam(value = "remark",required = false) String remark){
        CustomEcu  ecu = null;
        if(id != null){
            ecu=this.hibernateHandler.getEntityById(CustomEcu.class,id);
        }
        if(ecu == null){
            ecu=new CustomEcu();
        }
        ecu.setCode(code);
        ecu.setShowNameEn(showNameEn);
        ecu.setShowNameCn(showNameCn);
        ecu.setRemark(remark);
        if(ecu.getId()!=null){
            this.hibernateHandler.modifyEntity(ecu);
        }else{
            this.hibernateHandler.addEntity(ecu);
        }
    }


    /**
     * 查询列表
     */
    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    public QueryResult list(@RequestParam(value = "code",required = false) String code,
                            @RequestParam(value = "showNameEn",required = false) String showNameEn,
                            @RequestParam(value = "showNameCn",required = false) String showNameCn,
                            @RequestParam(value = "page",required = false,defaultValue = "1") int page,
                            @RequestParam(value = "rows",required = false,defaultValue = "20") int rows,
                            @RequestParam(value = "sort",required = false,defaultValue = "code") String sort,
                            @RequestParam(value = "order",required = false,defaultValue = "asc") String order){

        List<QueryParam> queryParamList=new ArrayList<>();
        if(code!=null && code.length()!=0){
            queryParamList.add(new QueryParam("and","code","like","%"+code+"%"));
        }
        if(showNameEn!=null && showNameEn.length()!=0){
            queryParamList.add(new QueryParam("and","showNameEn","like","%"+showNameEn+"%"));
        }
        if(showNameCn!=null && showNameCn.length()!=0){
            queryParamList.add(new QueryParam("and","showNameCn","like","%"+showNameCn+"%"));
        }

        QueryResult queryResult=new QueryResult(page,rows);
        queryResult.getOrders().put(sort,order);

        queryResult.setTotal(this.hibernateHandler.getEntityCountByQueryParam(CustomEcu.class, queryParamList));
        List<CustomEcu> rs=this.hibernateHandler.getEntityListByQueryParam(CustomEcu.class, queryParamList, queryResult.getStart(), queryResult.getCount(), queryResult.getOrders());

        Gson gson=new Gson();
        for(CustomEcu ecu:rs){
            String jsonStr=ecu.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        }

        return queryResult;
    }

    /**
     * 查询Ecu
     */
    @RequestMapping(method = RequestMethod.POST,value = "/queryEcu")
    @ResponseBody
    public QueryResult queryEcu(@RequestParam(value = "q",required = false) String keywords){
        QueryResult queryResult=new QueryResult();
        queryResult.getOrders().put("code", "asc");
        List<CustomEcu> rs;
        if(keywords!=null && keywords.length()>0){
            List<QueryParam> queryParamList=new ArrayList<>();
            queryParamList.add(new QueryParam("and (","code","like","%"+keywords+"%"));
            queryParamList.add(new QueryParam("or", "showNameCn", "like", "%"+keywords+"%"));
            queryParamList.add(new QueryParam("or", "showNameEn", "like", "%"+keywords+"%"));
            queryParamList.add(new QueryParam(") and", "id", "is", "not null"));

            rs=this.hibernateHandler.getEntityListByQueryParam(CustomEcu.class, queryParamList,queryResult.getOrders());
        }else{
            rs=this.hibernateHandler.getEntityListAll(CustomEcu.class, queryResult.getOrders());
        }
        Gson gson=new Gson();
        rs.stream().forEach(ecu->{
            String jsonStr=ecu.genEntityJsonStr();
            queryResult.getRows().add(gson.fromJson(jsonStr,Map.class));
        });
        return queryResult;
    }


}
