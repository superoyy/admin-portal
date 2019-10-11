package com.timanetworks.iov.web.handler.impl;

import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.core.sql.excecutor.SqlExecutor;
import com.timanetworks.iov.dao.SysModuleDao;
import com.timanetworks.iov.dao.SysModuleFunctionDao;
import com.timanetworks.iov.domain.SysModule;
import com.timanetworks.iov.domain.SysModuleFunction;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.handler.ModuleHandler;
import com.timanetworks.iov.web.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dukla on 10/26/16.
 */
@Service("moduleHandler")
public class ModuleHandlerImpl implements ModuleHandler {

    @Autowired
    private SysModuleDao sysModuleDao;

    @Autowired
    private SysModuleFunctionDao sysModuleFunctionDao;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    private UserHandler userHandler;

    /**
     * 得到最大模块排序号(根据upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysModuleOrder(String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("moduleOrder", "desc");
        List<SysModule> list=this.sysModuleDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getModuleOrder();
        }
        return maxOrder;

    }

    /**
     * 得到最小模块排序号(根据upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysModuleOrder(String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("moduleOrder", "asc");
        List<SysModule> list=this.sysModuleDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int minOrder=0;
        if(list.size()!=0){
            minOrder=list.get(0).getModuleOrder();
        }
        return minOrder;
    }

    /**
     * 保存SysModule
     */
    @Transactional
    @Override
    public SysModule saveSysModule(SysModule sysModule){
        if(sysModule==null){
            return sysModule;
        }
        if(sysModule.getModuleOrder()==null || sysModule.getModuleOrder() < 1){
            int order=this.getMaxSysModuleOrder(sysModule.getUpId())+1;
            sysModule.setModuleOrder(order);
        }
        if(sysModule.getId()==null || sysModule.getId().length()==0){
            int nodeLevel=0;
            if(sysModule.getUpId()!=null){
                nodeLevel= Kit.getObjInteger(this.sqlExecutor.getOneValue("select NODE_LEVEL from sys_module where ID=?", new Object[]{sysModule.getUpId()}));
            }
            sysModule.setNodeLevel(nodeLevel+1);

            int maxOrder=this.getMaxSysModuleOrder(sysModule.getUpId());
            sysModule.setModuleNo(this.getUqModuleNo(sysModule, maxOrder));

            sysModule.setId(this.sysModuleDao.insertEntity(sysModule));
        }else{
            this.sysModuleDao.updateEntity(sysModule);
        }
        return sysModule;
    }

    /*
     * 得到唯一模块编号
     */
    private String getUqModuleNo(SysModule sysModule,int maxOrder){
        String moduleNo;
        if(sysModule.getUpId()!=null){
            SysModule upModule=this.sysModuleDao.getEntityById(sysModule.getUpId());
            moduleNo=upModule.getModuleNo()+"_"+String.valueOf(maxOrder+1);
        }else{
            moduleNo="m_"+String.valueOf(maxOrder+1);
        }
        if(isUqModuleNo(moduleNo)){
            return moduleNo;
        }else{
            return getUqModuleNo(sysModule,maxOrder+1);
        }
    }

    /*
     * 判断唯一模块编号
     */
    private boolean isUqModuleNo(String moduleNo){
        int count= Kit.getObjInteger(this.sqlExecutor.getOneValue("select count(1) from sys_module where MODULE_NO = ?", new Object[]{moduleNo}));
        return count == 0;
    }


    /**
     * 删除模块
     */
    @Transactional
    @Override
    public void removeSysModule(String id){
        String sql="select ID from sys_module where UP_ID=?";
        List<Map<String,Object>> recs=this.sqlExecutor.getRecordsList(sql,new Object[]{id});
        for(Map<String,Object> rec:recs){
            //递归删除子模块
            removeSysModule(Kit.getObjStr(rec.get("ID")));
        }
        //删除权限
        this.userHandler.updatePermitByModuleId(id, null);
        //删除模块功能表
        sql="delete from sys_module_function where MODULE_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
        //更新兄弟模块顺序
        sql="select UP_ID,MODULE_ORDER from sys_module where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null){
            if(rec.get("UP_ID")!=null){
                sql="update sys_module set MODULE_ORDER=MODULE_ORDER-1 where UP_ID=? and MODULE_ORDER>?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("UP_ID"),rec.get("MODULE_ORDER")});
            }else{
                sql="update sys_module set MODULE_ORDER=MODULE_ORDER-1 where UP_ID is null and MODULE_ORDER>?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("MODULE_ORDER")});
            }
        }
        //删除模块表
        sql="delete from sys_module where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
    }


    /**
     * 调整模块顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysModuleOrder(SysModule sysModule,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysModuleOrder(sysModule.getUpId());
                if(min!=sysModule.getModuleOrder()){
                    if(sysModule.getUpId()!=null){
                        sql="update sys_module set MODULE_ORDER=MODULE_ORDER+1 where UP_ID=? and MODULE_ORDER < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getUpId(),sysModule.getModuleOrder()});
                    }else{
                        sql="update sys_module set MODULE_ORDER=MODULE_ORDER+1 where UP_ID is null and MODULE_ORDER < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder()});
                    }
                    sql="update sys_module set MODULE_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysModule.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysModuleOrder(sysModule.getUpId());
                if(min!=sysModule.getModuleOrder()){
                    if(sysModule.getUpId()!=null){
                        sql="update sys_module set MODULE_ORDER=? where UP_ID=? and MODULE_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder(),sysModule.getUpId(),sysModule.getModuleOrder()-1});
                    }else{
                        sql="update sys_module set MODULE_ORDER=? where UP_ID is null and MODULE_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder(),sysModule.getModuleOrder()-1});
                    }
                    sql="update sys_module set MODULE_ORDER=MODULE_ORDER-1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModule.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysModuleOrder(sysModule.getUpId());
                if(max!=sysModule.getModuleOrder()){
                    if(sysModule.getUpId()!=null){
                        sql="update sys_module set MODULE_ORDER=? where UP_ID=? and MODULE_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder(),sysModule.getUpId(),sysModule.getModuleOrder()+1});
                    }else{
                        sql="update sys_module set MODULE_ORDER=? where UP_ID is null and MODULE_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder(),sysModule.getModuleOrder()+1});
                    }
                    sql="update sys_module set MODULE_ORDER=MODULE_ORDER+1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModule.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysModuleOrder(sysModule.getUpId());
                if(max!=sysModule.getModuleOrder()){
                    if(sysModule.getUpId()!=null){
                        sql="update sys_module set MODULE_ORDER=MODULE_ORDER-1 where UP_ID=? and MODULE_ORDER > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getUpId(),sysModule.getModuleOrder()});
                    }else{
                        sql="update sys_module set MODULE_ORDER=MODULE_ORDER-1 where UP_ID is null and MODULE_ORDER > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysModule.getModuleOrder()});
                    }
                    sql="update sys_module set MODULE_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysModule.getId()});
                }
        }
    }

    /**
     * 得到最大功能项排序号(根据moduleId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysModuleFunctionOrder(String moduleId){
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("funcOrder", "desc");
        List<SysModuleFunction> list=this.sysModuleFunctionDao.getEntityListByProperty("sysModule.id", moduleId, 0, 1, orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getFuncOrder();
        }
        return maxOrder;
    }

    /**
     * 得到最小功能项排序号(根据moduleId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysModuleFunctionOrder(String moduleId){
        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("funcOrder", "asc");
        List<SysModuleFunction> list=this.sysModuleFunctionDao.getEntityListByProperty("sysModule.id", moduleId, 0, 1, orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getFuncOrder();
        }
        return maxOrder;
    }

    /**
     * 保存SysModuleFunction
     */
    @Transactional
    @Override
    public SysModuleFunction saveSysModuleFunction(SysModuleFunction sysModuleFunction){
        if(sysModuleFunction==null){
            return sysModuleFunction;
        }
        if(sysModuleFunction.getFuncOrder()==null || sysModuleFunction.getFuncOrder() < 1){
            int order=this.getMaxSysModuleFunctionOrder(sysModuleFunction.getSysModule().getId())+1;
            sysModuleFunction.setFuncOrder(order);
        }
        if(sysModuleFunction.getId()==null || sysModuleFunction.getId().length()==0){
            sysModuleFunction.setId(this.sysModuleFunctionDao.insertEntity(sysModuleFunction));
        }else{
            this.sysModuleFunctionDao.updateEntity(sysModuleFunction);
        }
        return sysModuleFunction;
    }

    /**
     * 删除功能项
     */
    @Transactional
    @Override
    public void removeSysModuleFunction(String id){
        //删除功能项权限
        this.userHandler.updateFuncPermitByFuncId(id, null);
        //更新兄弟模块顺序
        String sql="select MODULE_ID,FUNC_ORDER from sys_module_function where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null){
            sql="update sys_module_function set FUNC_ORDER=FUNC_ORDER-1 where MODULE_ID=? and FUNC_ORDER>?";
            this.sqlExecutor.execute(sql, new Object[]{rec.get("MODULE_ID"),rec.get("FUNC_ORDER")});
        }
        //删除功能项表
        sql="delete from sys_module_function where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
    }

    /**
     * 调整功能项顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysModuleFunctionOrder(SysModuleFunction sysModuleFunction,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysModuleFunctionOrder(sysModuleFunction.getSysModule().getId());
                if(min!=sysModuleFunction.getFuncOrder()){
                    sql="update sys_module_function set FUNC_ORDER=FUNC_ORDER+1 where MODULE_ID=? and FUNC_ORDER < ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getSysModule().getId(),sysModuleFunction.getFuncOrder()});
                    sql="update sys_module_function set FUNC_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysModuleFunction.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysModuleFunctionOrder(sysModuleFunction.getSysModule().getId());
                if(min!=sysModuleFunction.getFuncOrder()){
                    sql="update sys_module_function set FUNC_ORDER=? where MODULE_ID=? and FUNC_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getFuncOrder(),sysModuleFunction.getSysModule().getId(),sysModuleFunction.getFuncOrder()-1});
                    sql="update sys_module_function set FUNC_ORDER=FUNC_ORDER-1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysModuleFunctionOrder(sysModuleFunction.getSysModule().getId());
                if(max!=sysModuleFunction.getFuncOrder()){
                    sql="update sys_module_function set FUNC_ORDER=? where MODULE_ID=? and FUNC_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getFuncOrder(),sysModuleFunction.getSysModule().getId(),sysModuleFunction.getFuncOrder()+1});
                    sql="update sys_module_function set FUNC_ORDER=FUNC_ORDER+1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysModuleFunctionOrder(sysModuleFunction.getSysModule().getId());
                if(max!=sysModuleFunction.getFuncOrder()){
                    sql="update sys_module_function set FUNC_ORDER=FUNC_ORDER-1 where MODULE_ID=? and FUNC_ORDER > ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysModuleFunction.getSysModule().getId(),sysModuleFunction.getFuncOrder()});
                    sql="update sys_module_function set FUNC_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysModuleFunction.getId()});
                }
        }

    }

}
