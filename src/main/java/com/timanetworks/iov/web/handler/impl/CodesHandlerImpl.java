package com.timanetworks.iov.web.handler.impl;

import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.core.sql.excecutor.SqlExecutor;
import com.timanetworks.iov.dao.SysCodesDao;
import com.timanetworks.iov.domain.SysCodes;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.handler.CodesHandler;
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
@Service("codesHandler")
public class CodesHandlerImpl implements CodesHandler {

    @Autowired
    SysCodesDao sysCodesDao;

    @Autowired
    private SqlExecutor sqlExecutor;


    /**
     * 得到系统代码(根据Type)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public List<SysCodes> getSysCodesByType(String codeType) {
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("codeOrder", "asc");
        return this.sysCodesDao.getEntityListByProperty("codeType", codeType, orderProps);
    }

    /**
     * 得到系统代码(根据Key)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public SysCodes getSysCodesByKey(String codeType, String codeKey) {
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("codeOrder", "asc");
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("codeType", codeType);
        param.put("codeKey", codeKey);
        List<SysCodes> recs=this.sysCodesDao.getEntityListByPropertys(param, orderProps);
        return recs.size()!=0 ? recs.get(0) : null;
    }


    /**
     * 得到最大代码排序号(根据codeType,upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysCodesOrder(String codeType,String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        queryParamList.add(new QueryParam("and","codeType","=",codeType));
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("codeOrder", "desc");
        List<SysCodes> list=this.sysCodesDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getCodeOrder();
        }
        return maxOrder;
    }

    /**
     * 得到最小代码排序号(根据codeType,upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysCodesOrder(String codeType,String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        queryParamList.add(new QueryParam("and","codeType","=",codeType));
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("codeOrder", "asc");
        List<SysCodes> list=this.sysCodesDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int minOrder=0;
        if(list.size()!=0){
            minOrder=list.get(0).getCodeOrder();
        }
        return minOrder;
    }

    /**
     * 保存sysCodes
     */
    @Transactional
    @Override
    public SysCodes saveSysCodes(SysCodes sysCodes){
        if(sysCodes==null){
            return sysCodes;
        }
        if(sysCodes.getCodeType()==null || sysCodes.getCodeType().length()==0){
            return sysCodes;
        }
        if(sysCodes.getCodeKey()==null || sysCodes.getCodeKey().length()==0){
            int maxOrder=this.getMaxSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId());
            sysCodes.setCodeKey(this.getUqCodeKey(sysCodes, maxOrder));
        }
        if(sysCodes.getCodeState()==null || sysCodes.getCodeState().length()==0){
            sysCodes.setCodeState("0");
        }
        if(sysCodes.getCodeOrder()==null || sysCodes.getCodeOrder() < 1){
            int order=this.getMaxSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId())+1;
            sysCodes.setCodeOrder(order);
        }
        if(sysCodes.getId()==null || sysCodes.getId().length()==0){
            sysCodes.setId(this.sysCodesDao.insertEntity(sysCodes));
        }else{
            this.sysCodesDao.updateEntity(sysCodes);
        }
        return sysCodes;
    }

    /*
     * 得到唯一codes编号
     */
    private String getUqCodeKey(SysCodes sysCodes,int maxOrder){
        String codeKey;
        if(sysCodes.getUpId()!=null){
            SysCodes upCodes=this.sysCodesDao.getEntityById(sysCodes.getUpId());
            codeKey=upCodes.getCodeKey()+"_"+String.valueOf(maxOrder+1);
        }else{
            codeKey="key_"+String.valueOf(maxOrder+1);
        }
        if(isUqCodeKey(sysCodes.getCodeType(),codeKey)){
            return codeKey;
        }else{
            return getUqCodeKey(sysCodes,maxOrder+1);
        }
    }

    /*
     * 判断唯一codes编号
     */
    private boolean isUqCodeKey(String codeType,String codeKey){
        int count= Kit.getObjInteger(this.sqlExecutor.getOneValue("select count(1) from sys_codes where CODE_KEY = ? and CODE_TYPE=?", new Object[]{codeKey, codeType}));
        return count == 0;
    }


    /**
     * 删除sysCodes
     */
    @Transactional
    @Override
    public void removeSysCodes(String id){
        String sql="select ID from sys_codes where UP_ID=?";
        List<Map<String,Object>> recs=this.sqlExecutor.getRecordsList(sql,new Object[]{id});
        for(Map<String,Object> rec:recs){
            //递归删除孩子
            removeSysCodes(Kit.getObjStr(rec.get("ID")));
        }
        //更新兄弟顺序
        sql="select UP_ID,CODE_TYPE,CODE_ORDER from sys_codes where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null){
            if(rec.get("UP_ID")!=null){
                sql="update sys_codes set CODE_ORDER=CODE_ORDER-1 where UP_ID=? and CODE_ORDER > ? and CODE_TYPE=?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("UP_ID"),rec.get("CODE_ORDER"),rec.get("CODE_TYPE")});
            }else{
                sql="update sys_codes set CODE_ORDER=CODE_ORDER-1 where UP_ID is null and CODE_ORDER > ? and CODE_TYPE=?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("CODE_ORDER"),rec.get("CODE_TYPE")});
            }
        }
        //删除记录
        sql="delete from sys_codes where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
    }


    /**
     * 调整代码顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysCodesOrder(SysCodes sysCodes,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId());
                if(min!=sysCodes.getCodeOrder()){
                    if(sysCodes.getUpId()!=null){
                        sql="update sys_codes set code_order=code_order+1 where code_type=? and up_id=? and code_order < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeType(),sysCodes.getUpId(),sysCodes.getCodeOrder()});
                    }else{
                        sql="update sys_codes set code_order=code_order+1 where code_type=? and up_id is null and code_order < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeType(),sysCodes.getCodeOrder()});
                    }
                    sql="update sys_codes set code_order=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysCodes.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId());
                if(min!=sysCodes.getCodeOrder()){
                    if(sysCodes.getUpId()!=null){
                        sql="update sys_codes set code_order=? where code_type=? and up_id=? and code_order = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeOrder(),sysCodes.getCodeType(),sysCodes.getUpId(),sysCodes.getCodeOrder()-1});
                    }else{
                        sql="update sys_codes set code_order=? where code_type=? and up_id is null and code_order = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeOrder(),sysCodes.getCodeType(),sysCodes.getCodeOrder()-1});
                    }
                    sql="update sys_codes set code_order=code_order-1 where id=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysCodes.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId());
                if(max!=sysCodes.getCodeOrder()){
                    if(sysCodes.getUpId()!=null){
                        sql="update sys_codes set code_order=? where code_type=? and up_id=? and code_order = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeOrder(),sysCodes.getCodeType(),sysCodes.getUpId(),sysCodes.getCodeOrder()+1});
                    }else{
                        sql="update sys_codes set code_order=? where code_type=? and up_id is null and code_order = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeOrder(),sysCodes.getCodeType(),sysCodes.getCodeOrder()+1});
                    }
                    sql="update sys_codes set code_order=code_order+1 where id=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysCodes.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysCodesOrder(sysCodes.getCodeType(), sysCodes.getUpId());
                if(max!=sysCodes.getCodeOrder()){
                    if(sysCodes.getUpId()!=null){
                        sql="update sys_codes set code_order=code_order-1 where code_type=? and up_id=? and code_order > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeType(),sysCodes.getUpId(),sysCodes.getCodeOrder()});
                    }else{
                        sql="update sys_codes set code_order=code_order-1 where code_type=? and up_id is null and code_order > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysCodes.getCodeType(),sysCodes.getCodeOrder()});
                    }
                    sql="update sys_codes set code_order=? where id=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysCodes.getId()});
                }
        }
    }
}
