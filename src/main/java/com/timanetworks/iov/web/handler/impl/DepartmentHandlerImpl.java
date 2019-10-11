package com.timanetworks.iov.web.handler.impl;

import com.timanetworks.iov.core.jpa.dao.QueryParam;
import com.timanetworks.iov.core.sql.excecutor.SqlExecutor;
import com.timanetworks.iov.dao.SysDeptDao;
import com.timanetworks.iov.domain.SysDept;
import com.timanetworks.iov.util.Kit;
import com.timanetworks.iov.web.handler.DepartmentHandler;
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
@Service("departmentHandler")
public class DepartmentHandlerImpl implements DepartmentHandler {

    @Autowired
    private SysDeptDao sysDeptDao;

    @Autowired
    private SqlExecutor sqlExecutor;


    /**
     * 得到最大部门排序号(根据upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysDeptOrder(String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("deptOrder", "desc");
        List<SysDept> list=this.sysDeptDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getDeptOrder();
        }
        return maxOrder;
    }

    /**
     * 得到最小部门排序号(根据upId)
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysDeptOrder(String upId){
        List<QueryParam> queryParamList=new ArrayList<QueryParam>();
        if(upId==null || upId.length()==0){
            queryParamList.add(new QueryParam("and","upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","upId","=",upId));
        }
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("deptOrder", "asc");
        List<SysDept> list=this.sysDeptDao.getEntityListByQueryParam(queryParamList,0,1,orderProps);
        int minOrder=0;
        if(list.size()!=0){
            minOrder=list.get(0).getDeptOrder();
        }
        return minOrder;
    }

    /**
     * 保存SysDept
     */
    @Transactional
    @Override
    public SysDept saveSysDept(SysDept sysDept){
        if(sysDept==null){
            return sysDept;
        }
        if(sysDept.getDeptNo()==null || sysDept.getDeptNo().length()==0){
            int maxOrder=this.getMaxSysDeptOrder(sysDept.getUpId());
            sysDept.setDeptNo(this.getUqDeptNo(sysDept, maxOrder));
        }
        if(sysDept.getDeptOrder()==null || sysDept.getDeptOrder() < 1){
            int order=this.getMaxSysDeptOrder(sysDept.getUpId())+1;
            sysDept.setDeptOrder(order);
        }
        if(sysDept.getDeptState()==null || sysDept.getDeptState().length()==0){
            sysDept.setDeptState("0");
        }
        if(sysDept.getId()==null || sysDept.getId().length()==0){
            sysDept.setId(this.sysDeptDao.insertEntity(sysDept));
        }else{
            this.sysDeptDao.updateEntity(sysDept);
        }
        return sysDept;
    }

    /*
     * 得到唯一部门编号
     */
    private String getUqDeptNo(SysDept sysDept,int maxOrder){
        String deptNo;
        if(sysDept.getUpId()!=null){
            SysDept upDept=this.sysDeptDao.getEntityById(sysDept.getUpId());
            deptNo=upDept.getDeptNo()+"_"+String.valueOf(maxOrder+1);
        }else{
            deptNo="dept_"+String.valueOf(maxOrder+1);
        }
        if(isUqDeptNo(deptNo)){
            return deptNo;
        }else{
            return getUqDeptNo(sysDept,maxOrder+1);
        }
    }

    /*
     * 判断唯一菜单编号
     */
    private boolean isUqDeptNo(String deptNo){
        int count= Kit.getObjInteger(this.sqlExecutor.getOneValue("select count(1) from sys_dept where DEPT_NO = ?", new Object[]{deptNo}));
        return count == 0;
    }


    /**
     * 删除部门
     */
    @Transactional
    @Override
    public void removeSysDept(String id){
        String sql="select ID from sys_dept where UP_ID=?";
        List<Map<String,Object>> recs=this.sqlExecutor.getRecordsList(sql,new Object[]{id});
        for(Map<String,Object> rec:recs){
            //递归删除孩子
            removeSysDept(Kit.getObjStr(rec.get("ID")));
        }
        //更新兄弟顺序
        sql="select UP_ID,DEPT_ORDER from sys_dept where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null){
            if(rec.get("UP_ID")!=null){
                sql="update sys_dept set DEPT_ORDER=DEPT_ORDER-1 where UP_ID=? and DEPT_ORDER > ?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("UP_ID"),rec.get("DEPT_ORDER")});
            }else{
                sql="update sys_dept set DEPT_ORDER=DEPT_ORDER-1 where UP_ID is null and DEPT_ORDER > ?";
                this.sqlExecutor.execute(sql, new Object[]{rec.get("DEPT_ORDER")});
            }
        }
        //更新用户
        sql="update sys_login set DEPT_ID=null where DEPT_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
        //删除记录
        sql="delete from sys_dept where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
    }

    /**
     * 调整部门顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysDeptOrder(SysDept sysDept,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysDeptOrder(sysDept.getUpId());
                if(min!=sysDept.getDeptOrder()){
                    if(sysDept.getUpId()!=null){
                        sql="update sys_dept set DEPT_ORDER=DEPT_ORDER+1 where UP_ID=? and DEPT_ORDER < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getUpId(),sysDept.getDeptOrder()});
                    }else{
                        sql="update sys_dept set DEPT_ORDER=DEPT_ORDER+1 where UP_ID is null and DEPT_ORDER < ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder()});
                    }
                    sql="update sys_dept set DEPT_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysDept.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysDeptOrder(sysDept.getUpId());
                if(min!=sysDept.getDeptOrder()){
                    if(sysDept.getUpId()!=null){
                        sql="update sys_dept set DEPT_ORDER=? where UP_ID=? and DEPT_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder(),sysDept.getUpId(),sysDept.getDeptOrder()-1});
                    }else{
                        sql="update sys_dept set DEPT_ORDER=? where UP_ID is null and DEPT_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder(),sysDept.getDeptOrder()-1});
                    }
                    sql="update sys_dept set DEPT_ORDER=DEPT_ORDER-1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysDept.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysDeptOrder(sysDept.getUpId());
                if(max!=sysDept.getDeptOrder()){
                    if(sysDept.getUpId()!=null){
                        sql="update sys_dept set DEPT_ORDER=? where UP_ID=? and DEPT_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder(),sysDept.getUpId(),sysDept.getDeptOrder()+1});
                    }else{
                        sql="update sys_dept set DEPT_ORDER=? where UP_ID is null and DEPT_ORDER = ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder(),sysDept.getDeptOrder()+1});
                    }
                    sql="update sys_dept set DEPT_ORDER=DEPT_ORDER+1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysDept.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysDeptOrder(sysDept.getUpId());
                if(max!=sysDept.getDeptOrder()){
                    if(sysDept.getUpId()!=null){
                        sql="update sys_dept set DEPT_ORDER=DEPT_ORDER-1 where UP_ID=? and DEPT_ORDER > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getUpId(),sysDept.getDeptOrder()});
                    }else{
                        sql="update sys_dept set DEPT_ORDER=DEPT_ORDER-1 where UP_ID is null and DEPT_ORDER > ?";
                        this.sqlExecutor.execute(sql, new Object[]{sysDept.getDeptOrder()});
                    }
                    sql="update sys_dept set DEPT_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysDept.getId()});
                }
        }
    }
}
