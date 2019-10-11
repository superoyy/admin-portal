package com.dukla.portal.admin.handler.impl;

import com.dukla.base.dao.*;
import com.dukla.base.domain.*;
import com.dukla.base.jpa.dao.QueryParam;
import com.dukla.base.sql.excecutor.SqlExecutor;
import com.dukla.base.util.Kit;
import com.dukla.portal.admin.handler.UserHandler;
import com.dukla.web.pojo.ModuleTreeVo;
import com.dukla.web.pojo.ModuleVo;
import com.dukla.web.pojo.UserModuleListVo;
import com.dukla.web.pojo.UserModuleTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dukla on 10/26/16.
 */
@Service("userHandler")
public class UserHandlerImpl implements UserHandler {

    @Autowired
    private SysLoginDao sysLoginDao;

    @Autowired
    private SysLoginRoleDao sysLoginRoleDao;

    @Autowired
    private SysPermitDao sysPermitDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysPermitFunctionDao sysPermitFunctionDao;

    @Autowired
    private SysModuleFunctionDao sysModuleFunctionDao;

    @Autowired
    private SqlExecutor sqlExecutor;

    /**
     * 得到用户的功能结构树
     * @param sysLogin
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public UserModuleTreeVo getUserModuleTree(SysLogin sysLogin){
        UserModuleTreeVo userModuleTreeVo=new UserModuleTreeVo();
        userModuleTreeVo.setSysLogin(sysLogin);

        Map<String,Object> param=new HashMap<>();
        param.put("sysLogin.id", sysLogin.getId());
        param.put("sysRole.roleState", "0");

        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("sysRole.roleOrder", "asc");

        //得到用户-角色集合
        List<SysLoginRole> sysLoginRoleList=this.sysLoginRoleDao.getEntityListByPropertys(param, orderProps);
        List<SysRole> sysRoleList=new ArrayList<>();
        orderProps.clear();
        orderProps.put("sysModule.moduleOrder", "asc");
        for(SysLoginRole sysLoginRole:sysLoginRoleList){
            sysRoleList.add(sysLoginRole.getSysRole());
            ModuleTreeVo newTreeVo=this.getRoleModuleTree(sysLoginRole.getSysRole());
            if(userModuleTreeVo.getModuleTreeVo()==null){
                userModuleTreeVo.setModuleTreeVo(newTreeVo);
            }else{
                //合并功能树
                userModuleTreeVo.setModuleTreeVo(this.mergerModuleTree(userModuleTreeVo.getModuleTreeVo(),newTreeVo));
            }
        }
        userModuleTreeVo.setSysRoleList(sysRoleList);
        return userModuleTreeVo;
    }

    /**
     * 得到角色模块树
     * @param sysRole
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public ModuleTreeVo getRoleModuleTree(SysRole sysRole){
        ModuleTreeVo moduleTreeVo=new ModuleTreeVo();
        this.getSubModuleTreeVo(moduleTreeVo, sysRole);
        return moduleTreeVo;
    }


    /*
     * 内部方法,递归
     * 得到角色模块子树
     */
    private void getSubModuleTreeVo(ModuleTreeVo moduleTreeVo,SysRole sysRole){
        List<QueryParam> queryParamList=new ArrayList<>();
        queryParamList.add(new QueryParam("and", "sysRole.id", "=", sysRole.getId()));
        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("sysModule.moduleOrder", "asc");
        List<ModuleTreeVo> subModuleTreeVoList=new ArrayList<ModuleTreeVo>();
        if(moduleTreeVo.getSysModule()==null){//根节点
            queryParamList.add(new QueryParam("and","sysModule.upId","is","null"));
        }else{
            queryParamList.add(new QueryParam("and","sysModule.upId","=",moduleTreeVo.getSysModule().getId()));
            queryParamList.add(new QueryParam("and","sysModule.moduleState","=","0"));
        }
        List<SysPermit> sysPermitList=this.sysPermitDao.getEntityListByQueryParam(queryParamList,orderProps);
        for(SysPermit sysPermit:sysPermitList){
            ModuleTreeVo subModuleTreeVo=new ModuleTreeVo();
            subModuleTreeVo.setParentModuleTreeVo(moduleTreeVo);
            subModuleTreeVo.setSysModule(sysPermit.getSysModule());
            subModuleTreeVo.setSysModuleFunctionList(this.getSysModuleFunctionListByPermit(sysPermit));
            getSubModuleTreeVo(subModuleTreeVo,sysRole);//递归
            subModuleTreeVoList.add(subModuleTreeVo);
        }
        moduleTreeVo.setSubModuleTreeVoList(subModuleTreeVoList);
    }

    /*
     * 内部方法
     * 得到权限点功能集合
     */
    private List<SysModuleFunction> getSysModuleFunctionListByPermit(SysPermit sysPermit){
        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("sysModuleFunction.funcOrder", "asc");
        List<SysPermitFunction> sysPermitFunctionList=this.sysPermitFunctionDao.getEntityListByProperty("sysPermit.id", sysPermit.getId(), orderProps);
        List<SysModuleFunction> sysModuleFunctionList=new ArrayList<>();
        for(SysPermitFunction sysPermitFunction:sysPermitFunctionList){
            sysModuleFunctionList.add(sysPermitFunction.getSysModuleFunction());
        }
        return sysModuleFunctionList;
    }

    /*
     * 内部方法,递归
     * 合并功能树
     */
    private ModuleTreeVo mergerModuleTree(ModuleTreeVo orgTreeVo,ModuleTreeVo newTreeVo){
        ModuleTreeVo treeVo=orgTreeVo;
        //遍历新树
        for(ModuleTreeVo newNode:newTreeVo.getSubModuleTreeVoList()){
            this.addModuleNode(newNode, orgTreeVo);
            //合并子树
            treeVo=mergerModuleTree(treeVo,newNode);//递归
        }
        return treeVo;
    }

    /*
     * 内部方法,递归
     * 添加模块到功能树
     */
    private void addModuleNode(ModuleTreeVo newNode,ModuleTreeVo orgTreeVo){
        if(newNode.getParentModuleTreeVo().getSysModule()==null){//一级节点
            if(!this.isChildNode(newNode, orgTreeVo)){
                orgTreeVo.getSubModuleTreeVoList().add(newNode);
                Collections.sort(orgTreeVo.getSubModuleTreeVoList(), new ModuleTreeVoComparator());
            }
            return;
        }
        for(ModuleTreeVo orgNode:orgTreeVo.getSubModuleTreeVoList()){
            if(orgNode.getSysModule().getId().equals(newNode.getParentModuleTreeVo().getSysModule().getId())){
                if(!this.isChildNode(newNode, orgNode)){
                    orgNode.getSubModuleTreeVoList().add(newNode);
                    Collections.sort(orgNode.getSubModuleTreeVoList(), new ModuleTreeVoComparator());
                }
                return;
            }
            addModuleNode(newNode,orgNode);//递归
        }
    }

    /*
     * 内部方法
     * 判断模块节点是否是孩子节点
     */
    private boolean isChildNode(ModuleTreeVo node,ModuleTreeVo parentNode){
        for(ModuleTreeVo cNode:parentNode.getSubModuleTreeVoList()){
            if(cNode.getSysModule().getId().equals(node.getSysModule().getId())){
                return true;
            }
        }
        return false;
    }

    /*
     * 内部类
     * 模块树集合排序器
     */
    private class ModuleTreeVoComparator implements Comparator<ModuleTreeVo>{
        @Override
        public int compare(ModuleTreeVo m1, ModuleTreeVo m2) {
            return m1.getSysModule().getModuleOrder()-m2.getSysModule().getModuleOrder();
        }
    }

    /**
     * 得到用户功能列表
     * @param sysLogin
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public UserModuleListVo getUserModuleList(SysLogin sysLogin) {
        UserModuleListVo userModuleListVo=new UserModuleListVo();
        userModuleListVo.setSysLogin(sysLogin);
        List<ModuleVo> moduleVoList=new ArrayList<>();


        Map<String,Object> param=new HashMap<>();
        param.put("sysLogin.id", sysLogin.getId());
        param.put("sysRole.roleState", "0");

        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("sysRole.roleOrder", "asc");


        //得到用户-角色集合
        List<SysLoginRole> sysLoginRoleList=this.sysLoginRoleDao.getEntityListByPropertys(param, orderProps);
        List<SysRole> sysRoleList=new ArrayList<>();
        for(SysLoginRole sysLoginRole:sysLoginRoleList){
            sysRoleList.add(sysLoginRole.getSysRole());
            //得到角色功能集合
            List<ModuleVo> roleModuleVoList=this.getRoleModuleList(sysLoginRole.getSysRole());
            //合并功能集合
            moduleVoList=this.mergerModuleList(moduleVoList, roleModuleVoList);
        }
        userModuleListVo.setSysRoleList(sysRoleList);
        userModuleListVo.setModuleVoList(moduleVoList);

        return userModuleListVo;
    }

    /**
     * 得到角色功能列表
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public List<ModuleVo> getRoleModuleList(SysRole sysRole){
        List<ModuleVo> moduleVoList=new ArrayList<>();
        List<QueryParam> queryParamList=new ArrayList<>();
        queryParamList.add(new QueryParam("and","sysRole.id","=",sysRole.getId()));
        queryParamList.add(new QueryParam("and","sysModule.moduleState","=","0"));
        Map<String,String> orderProps=new HashMap<>();
        orderProps.put("sysModule.moduleOrder", "asc");
        orderProps.put("sysModule.nodeLevel", "asc");
        List<SysPermit> sysPermitList=this.sysPermitDao.getEntityListByQueryParam(queryParamList,orderProps);
        orderProps.clear();
        orderProps.put("sysModuleFunction.funcOrder", "asc");
        for(SysPermit sysPermit:sysPermitList){
            ModuleVo moduleVo=new ModuleVo();
            List<SysModuleFunction> sysModuleFunctionList=new ArrayList<>();
            moduleVo.setSysModule(sysPermit.getSysModule());
            //取权限功能点
            List<SysPermitFunction> permitFuncList=this.sysPermitFunctionDao.getEntityListByProperty("sysPermit.id", sysPermit.getId(),orderProps);
            for(SysPermitFunction permitFunc:permitFuncList){
                sysModuleFunctionList.add(permitFunc.getSysModuleFunction());
            }
            moduleVo.setSysModuleFunctionList(sysModuleFunctionList);
            moduleVoList.add(moduleVo);
        }
        return moduleVoList;
    }

    /*
     * 内部方法
     * 合并模块集合
     */
    private List<ModuleVo> mergerModuleList(List<ModuleVo> orgVoList,List<ModuleVo> newVoList){
        boolean needResort=false;
        if(orgVoList.size()==0){
            return newVoList;
        }else{
            for(ModuleVo newVo:newVoList){
                boolean found=false;
                for(ModuleVo orgVo:orgVoList){
                    if(orgVo.getSysModule().getId().equals(newVo.getSysModule().getId())){
                        //合并功能点
                        orgVo.setSysModuleFunctionList(this.mergerModuleFunction(orgVo.getSysModuleFunctionList(), newVo.getSysModuleFunctionList()));
                        found=true;
                        break;
                    }
                }
                if(!found){
                    orgVoList.add(newVo);
                    needResort=true;
                }
            }
        }
        //需要重新排序
        if(needResort){
            Collections.sort(orgVoList, new ModuleVoComparator());
        }
        return orgVoList;
    }

    /*
     * 内部方法
     * 合并功能点集合
     */
    private List<SysModuleFunction> mergerModuleFunction(List<SysModuleFunction> orgFuncList,List<SysModuleFunction> newFuncList){
        boolean needResort=false;
        if(orgFuncList.size()==0){
            return newFuncList;
        }else{
            for(SysModuleFunction newFunc:newFuncList){
                boolean found=false;
                for(SysModuleFunction orgFunc:orgFuncList){
                    if(orgFunc.getId().equals(newFunc.getId())){
                        found=true;
                        break;
                    }
                }
                if(!found){
                    orgFuncList.add(newFunc);
                    needResort=true;
                }
            }
        }
        //需要重新排序
        if(needResort){
            Collections.sort(orgFuncList, new ModuleFunctionComparator());
        }
        return orgFuncList;

    }

    /*
     * 内部类
     * 模块值对象排序器
     */
    private class ModuleVoComparator implements Comparator<ModuleVo>{
        @Override
        public int compare(ModuleVo m1, ModuleVo m2) {
            return m1.getSysModule().getModuleOrder()-m2.getSysModule().getModuleOrder();
        }
    }

    /*
     * 内部类
     * 功能集合排序器
     */
    private class ModuleFunctionComparator implements Comparator<SysModuleFunction>{
        @Override
        public int compare(SysModuleFunction f1, SysModuleFunction f2) {
            return f1.getFuncOrder()-f2.getFuncOrder();
        }
    }



    /**
     * 根据模块调整权限
     */
    @Transactional
    @Override
    public void updatePermitByModuleId(String moduleId,String[] roleIds){
        String sql;
        if(moduleId==null || moduleId.length()==0){
            return;
        }
        //1、无权限
        if(roleIds==null || roleIds.length==0){
            sql="select ID from sys_permit where MODULE_ID=?";
            List<Map<String,Object>> permits=this.sqlExecutor.getRecordsList(sql, new Object[]{moduleId});
            for(Map<String,Object> permit:permits){
                this.removePermit(Kit.getObjStr(permit.get("ID")));
            }
            return;
        }
        //2、有权限
        //取不包含的权限集合
        StringBuilder sqlSb=new StringBuilder();
        String[] params=new String[roleIds.length+1];
        sqlSb.append("select ID from sys_permit where ROLE_ID not in(");
        for(int i=0;i<roleIds.length;i++){
            sqlSb.append(i!=roleIds.length-1 ? "?," : "?)");
            params[i]=roleIds[i];
        }
        sqlSb.append(" and MODULE_ID=?");
        params[roleIds.length]=moduleId;
        List<Map<String,Object>> recs=this.sqlExecutor.getRecordsList(sqlSb.toString(), params);
        //删除不包含的权限
        for(Map<String,Object> rec:recs){
            this.removePermit(Kit.getObjStr(rec.get("ID")));
        }
        //添加权限
        for(String roleId:roleIds){
            this.addPermit(roleId, moduleId);
        }
    }

    /**
     * 根据角色调整权限
     */
    @Transactional
    @Override
    public void updatePermitByRoleId(String roleId,String[] moduleIds){
        String sql;
        if(roleId==null || roleId.length()==0){
            return;
        }
        //1、没有权限
        if(moduleIds==null || moduleIds.length==0){
            sql="select ID from sys_permit where ROLE_ID=?";
            List<Map<String,Object>> permits=this.sqlExecutor.getRecordsList(sql, new Object[]{roleId});
            for(Map<String,Object> permit:permits){
                this.removePermit(Kit.getObjStr(permit.get("ID")));
            }
            return;
        }
        //2、有权限
        StringBuilder sqlSb=new StringBuilder();
        String[] params=new String[moduleIds.length+1];
        sqlSb.append("select ID from sys_permit where MODULE_ID not in(");
        for(int i=0;i<moduleIds.length;i++){
            sqlSb.append(i!=moduleIds.length-1 ? "?," : "?)");
            params[i]=moduleIds[i];
        }
        sqlSb.append(" and ROLE_ID=?");
        params[moduleIds.length]=roleId;
        List<Map<String,Object>> recs=this.sqlExecutor.getRecordsList(sqlSb.toString(), params);
        //删除不包含的权限
        for(Map<String,Object> rec:recs){
            this.removePermit(Kit.getObjStr(rec.get("ID")));
        }
        //添加权限
        for(String moduleId:moduleIds){
            this.addPermit(roleId, moduleId);
        }
    }

    /**
     * 增加权限
     */
    @Transactional
    @Override
    public String addPermit(String roleId,String moduleId){
        String sql="select * from sys_permit where ROLE_ID=? and MODULE_ID=?";
        Map<String,Object> permit=this.sqlExecutor.getOneRecord(sql,new Object[]{roleId,moduleId});
        String id;
        if(permit!=null && permit.get("ID")!=null){
            id= Kit.getObjStr(permit.get("ID"));
        }else{
            id= Kit.get36UUID();
            sql="insert into sys_permit(ID,ROLE_ID,MODULE_ID) values (?,?,?)";
            this.sqlExecutor.execute(sql, new Object[]{id,roleId,moduleId});
        }
        sql="select UP_ID from sys_module where ID=?";
        String upId= Kit.getObjStr(this.sqlExecutor.getOneValue(sql, new Object[]{moduleId}));
        if(upId!=null && upId.length()!=0){
            //递归添加上级节点权限
            addPermit(roleId,upId);
        }
        return id;
    }


    /**
     * 删除权限
     */
    @Transactional
    @Override
    public void removePermit(String id){
        String sql="select * from sys_permit where ID=?";
        Map<String,Object> permit=this.sqlExecutor.getOneRecord(sql, new Object[]{id});
        if(permit!=null && permit.get("ID")!=null){
            sql="select ID from sys_module where UP_ID=?";
            List<Map<String,Object>> subModules=this.sqlExecutor.getRecordsList(sql, new Object[]{permit.get("MODULE_ID")});
            for(Map<String,Object> subModule:subModules){
                sql="select ID from sys_permit where MODULE_ID=? and ROLE_ID=?";
                List<Map<String,Object>> subPermits=this.sqlExecutor.getRecordsList(sql, new Object[]{subModule.get("ID"),permit.get("ROLE_ID")});
                for(Map<String,Object> subPermit:subPermits){
                    //递归删除孩子节点的权限
                    removePermit(Kit.getObjStr(subPermit.get("ID")));
                }
            }
            //删除功能项权限
            sql="delete from sys_permit_function where PERMIT_ID=?";
            this.sqlExecutor.execute(sql, new Object[]{id});
            //删除权限
            sql="delete from sys_permit where ID=?";
            this.sqlExecutor.execute(sql, new Object[]{id});
        }
    }

    /**
     * 删除权限
     */
    @Transactional
    @Override
    public void removePermit(String roleId,String moduleId){
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("sysRole.id", roleId);
        param.put("sysModule.id", moduleId);
        List<SysPermit> list=this.sysPermitDao.getEntityListByPropertys(param, null);
        if(list.size()!=0){
            this.removePermit(list.get(0).getId());
        }
    }


    /**
     * 更新功能项权限(根据功能Id)
     */
    @Transactional
    @Override
    public void updateFuncPermitByFuncId(String funcId,String[] roleIds){
        if(funcId==null || funcId.length()==0){
            return;
        }
        //1、清除功能项权限
        String sql="delete from sys_permit_function where FUNC_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{funcId});
        //2、重新添加功能项权限
        if(roleIds!=null && roleIds.length!=0){
            sql="select * from sys_module_function where id=?";
            Map<String,Object> moduleFunc=this.sqlExecutor.getOneRecord(sql, new Object[]{funcId});
            if(moduleFunc==null){
                return;
            }
            for(String roleId:roleIds){
                this.addFuncPermit(roleId,funcId);
            }
        }
    }

    /**
     * 更新功能项权限(根据角色Id)
     */
    @Transactional
    @Override
    public void updateFuncPermitByRoleId(String roleId,String[] funcIds){
        if(roleId==null || roleId.length()==0){
            return;
        }
        //1、清除功能项权限
        String sql="delete from sys_permit_function where PERMIT_ID in (select ID from sys_permit where ROLE_ID=?)";
        this.sqlExecutor.execute(sql, new Object[]{roleId});
        //2、重新添加功能项权限
        if(funcIds!=null && funcIds.length!=0){
            for(String funcId:funcIds){
                this.addFuncPermit(roleId,funcId);
            }
        }
    }

    /**
     * 增加功能项权限
     */
    @Transactional
    @Override
    public String addFuncPermit(String roleId,String funcId){
        String id=null;
        SysModuleFunction sysModuleFunction=this.sysModuleFunctionDao.getEntityById(funcId);
        if(sysModuleFunction!=null){
            //首先添加权限
            String permitId=this.addPermit(roleId, sysModuleFunction.getSysModule().getId());
            //添加功能项权限
            Map<String,Object> param=new HashMap<String,Object>();
            param.put("sysModuleFunction.id", funcId);
            param.put("sysPermit.id", permitId);
            List<SysPermitFunction> list=this.sysPermitFunctionDao.getEntityListByPropertys(param,null);
            if(list.size()==0){
                id= Kit.get36UUID();
                this.sqlExecutor.execute("insert into sys_permit_function(ID,PERMIT_ID,FUNC_ID) values (?,?,?)", new Object[]{id,permitId,funcId});
            }else{
                id=list.get(0).getId();
            }
        }
        return id;
    }

    /**
     * 删除功能项权限
     */
    @Transactional
    @Override
    public void removeFuncPermit(String roleId,String funcId){
        SysModuleFunction sysModuleFunction=this.sysModuleFunctionDao.getEntityById(funcId);
        if(sysModuleFunction!=null){
            String sql="delete from sys_permit_function where PERMIT_ID in (select ID from sys_permit where ROLE_ID=? and MODULE_ID=?) and FUNC_ID=?";
            this.sqlExecutor.execute(sql, new Object[]{roleId,sysModuleFunction.getSysModule().getId(),funcId});
        }
    }

    /**
     * 得到最大角色排序号
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysRoleOrder(){
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("roleOrder", "desc");
        List<SysRole> list=this.sysRoleDao.getEntityListAll(0, 1, orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getRoleOrder();
        }
        return maxOrder;
    }

    /**
     * 得到最小角色排序号
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysRoleOrder(){
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("roleOrder", "asc");
        List<SysRole> list=this.sysRoleDao.getEntityListAll(0, 1, orderProps);
        int minOrder=0;
        if(list.size()!=0){
            minOrder=list.get(0).getRoleOrder();
        }
        return minOrder;
    }

    /**
     * 保存SysRole
     */
    @Transactional
    @Override
    public SysRole saveSysRole(SysRole sysRole){
        if(sysRole==null){
            return sysRole;
        }
        if(sysRole.getRoleState()==null || sysRole.getRoleState().length()==0){
            sysRole.setRoleState("0");
        }
        if(sysRole.getRoleOrder()==null || sysRole.getRoleOrder() < 1){
            int order=this.getMaxSysRoleOrder()+1;
            sysRole.setRoleOrder(order);
        }
        if(sysRole.getId()==null || sysRole.getId().length()==0){
            sysRole.setId(this.sysRoleDao.insertEntity(sysRole));
        }else{
            this.sysRoleDao.updateEntity(sysRole);
        }
        return sysRole;
    }

    /**
     * 删除角色
     */
    @Transactional
    @Override
    public void removeSysRole(String id){
        //删除权限
        this.updatePermitByRoleId(id, null);
        //删除角色用户表
        String sql="delete from sys_login_role where ROLE_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
        //更新其他角色顺序
        sql="select ID,ROLE_ORDER from sys_role where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null && rec.get("ROLE_ORDER")!=null){
            sql="update sys_role set ROLE_ORDER=ROLE_ORDER-1 where ROLE_ORDER>?";
            this.sqlExecutor.execute(sql, new Object[]{rec.get("ROLE_ORDER")});
        }
        //删除角色表
        sql="delete from sys_role where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
    }

    /**
     * 调整角色顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysRoleOrder(SysRole sysRole,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysRoleOrder();
                if(min!=sysRole.getRoleOrder()){
                    sql="update sys_role set ROLE_ORDER=ROLE_ORDER+1 where ROLE_ORDER < ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getRoleOrder()});
                    sql="update sys_role set ROLE_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysRole.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysRoleOrder();
                if(min!=sysRole.getRoleOrder()){
                    sql="update sys_role set ROLE_ORDER=? where ROLE_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getRoleOrder(),sysRole.getRoleOrder()-1});
                    sql="update sys_role set ROLE_ORDER=ROLE_ORDER-1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysRoleOrder();
                if(max!=sysRole.getRoleOrder()){
                    sql="update sys_role set ROLE_ORDER=? where ROLE_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getRoleOrder(),sysRole.getRoleOrder()+1});
                    sql="update sys_role set ROLE_ORDER=ROLE_ORDER+1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysRoleOrder();
                if(max!=sysRole.getRoleOrder()){
                    sql="update sys_role set ROLE_ORDER=ROLE_ORDER-1 where ROLE_ORDER > ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysRole.getRoleOrder()});
                    sql="update sys_role set ROLE_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysRole.getId()});
                }
        }
    }

    /**
     * 得到最大用户排序号
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMaxSysLoginOrder(){
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("loginOrder", "desc");
        List<SysLogin> list=this.sysLoginDao.getEntityListAll(0, 1, orderProps);
        int maxOrder=0;
        if(list.size()!=0){
            maxOrder=list.get(0).getLoginOrder();
        }
        return maxOrder;
    }

    /**
     * 得到最小用户排序号
     */
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    @Override
    public int getMinSysLoginOrder(){
        Map<String,String> orderProps=new HashMap<String,String>();
        orderProps.put("loginOrder", "asc");
        List<SysLogin> list=this.sysLoginDao.getEntityListAll(0, 1, orderProps);
        int minOrder=0;
        if(list.size()!=0){
            minOrder=list.get(0).getLoginOrder();
        }
        return minOrder;
    }

    /**
     * 保存用户
     */
    @Transactional
    @Override
    public void saveSysLogin(SysLogin sysLogin){
        if(sysLogin==null){
            return;
        }
        if(sysLogin.getLoginState()==null || sysLogin.getLoginState().length()==0){
            sysLogin.setLoginState("0");
        }
        if(sysLogin.getLoginOrder()==null || sysLogin.getLoginOrder() < 1){
            int order=this.getMaxSysLoginOrder()+1;
            sysLogin.setLoginOrder(order);
        }
        if(sysLogin.getId()==null || sysLogin.getId().length()==0){
            sysLogin.setId(this.sysLoginDao.insertEntity(sysLogin));
        }else{
            this.sysLoginDao.updateEntity(sysLogin);
        }
    }

    /**
     * 删除用户
     */
    @Transactional
    @Override
    public void removeSysLogin(String id){
        //删除角色用户
        String sql="delete from sys_login_role where LOGIN_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});
        //更新其他用户顺序
        sql="select ID,LOGIN_ORDER from sys_login where ID=?";
        Map<String,Object> rec=this.sqlExecutor.getOneRecord(sql,new Object[]{id});
        if(rec!=null && rec.get("LOGIN_ORDER")!=null){
            sql="update sys_login set LOGIN_ORDER=LOGIN_ORDER-1 where LOGIN_ORDER>?";
            this.sqlExecutor.execute(sql, new Object[]{rec.get("LOGIN_ORDER")});
        }
        //删除用户
        sql="delete from sys_login where ID=?";
        this.sqlExecutor.execute(sql, new Object[]{id});

    }

    /**
     * 调整用户顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    @Transactional
    @Override
    public void updateSysLoginOrder(SysLogin sysLogin,int direct){
        int min;
        int max;
        String sql;
        switch(direct){
            case 0://顶
                min=this.getMinSysLoginOrder();
                if(min!=sysLogin.getLoginOrder()){
                    sql="update sys_login set LOGIN_ORDER=LOGIN_ORDER+1 where LOGIN_ORDER < ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getLoginOrder()});
                    sql="update sys_login set LOGIN_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{min,sysLogin.getId()});
                }
                break;
            case 1://上
                min=this.getMinSysLoginOrder();
                if(min!=sysLogin.getLoginOrder()){
                    sql="update sys_login set LOGIN_ORDER=? where LOGIN_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getLoginOrder(),sysLogin.getLoginOrder()-1});
                    sql="update sys_login set LOGIN_ORDER=LOGIN_ORDER-1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getId()});
                }
                break;
            case 2://下
                max=this.getMaxSysLoginOrder();
                if(max!=sysLogin.getLoginOrder()){
                    sql="update sys_login set LOGIN_ORDER=? where LOGIN_ORDER = ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getLoginOrder(),sysLogin.getLoginOrder()+1});
                    sql="update sys_login set LOGIN_ORDER=LOGIN_ORDER+1 where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getId()});
                }
                break;
            case 3://底
                max=this.getMaxSysLoginOrder();
                if(max!=sysLogin.getLoginOrder()){
                    sql="update sys_login set LOGIN_ORDER=LOGIN_ORDER-1 where LOGIN_ORDER > ?";
                    this.sqlExecutor.execute(sql, new Object[]{sysLogin.getLoginOrder()});
                    sql="update sys_login set LOGIN_ORDER=? where ID=?";
                    this.sqlExecutor.execute(sql, new Object[]{max,sysLogin.getId()});
                }
        }
    }

    /**
     * 增加角色用户
     */
    @Transactional
    @Override
    public String addLoginRole(String roleId,String loginId){
        String sql="select * from sys_login_role where ROLE_ID=? and LOGIN_ID=?";
        Map<String,Object> loginRole=this.sqlExecutor.getOneRecord(sql,new Object[]{roleId,loginId});
        String id;
        if(loginRole!=null && loginRole.get("ID")!=null){
            id= Kit.getObjStr(loginRole.get("ID"));
        }else{
            id= Kit.get36UUID();
            sql="insert into sys_login_role(ID,ROLE_ID,LOGIN_ID) values (?,?,?)";
            this.sqlExecutor.execute(sql, new Object[]{id,roleId,loginId});
        }
        return id;
    }

    /**
     * 删除角色用户
     */
    @Transactional
    @Override
    public void removeLoginRole(String roleId,String loginId){
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("sysRole.id", roleId);
        param.put("sysLogin.id", loginId);
        this.sysLoginRoleDao.deleteEntityByPropertys(param);
    }

    /**
     * 批量修改角色用户(根据角色id)
     */
    @Transactional
    @Override
    public void updateLoginRoleByRoleId(String roleId,String[] loginIds){
        String sql="delete from sys_login_role where ROLE_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{roleId});
        if(loginIds!=null && loginIds.length!=0){
            for(String loginId:loginIds){
                this.addLoginRole(roleId, loginId);
            }
        }
    }

    /**
     * 批量修改角色用户(根据用户id)
     */
    @Transactional
    @Override
    public void updateLoginRoleByLoginId(String loginId,String[] roleIds){
        String sql="delete from sys_login_role where LOGIN_ID=?";
        this.sqlExecutor.execute(sql, new Object[]{loginId});
        if(roleIds!=null && roleIds.length!=0){
            for(String roleId:roleIds){
                this.addLoginRole(roleId, loginId);
            }
        }
    }

}
