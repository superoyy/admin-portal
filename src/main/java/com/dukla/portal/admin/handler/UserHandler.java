package com.dukla.portal.admin.web.handler;

import com.timanetworks.iov.domain.SysLogin;
import com.timanetworks.iov.domain.SysRole;
import com.dukla.web.pojo.ModuleTreeVo;
import com.dukla.web.pojo.ModuleVo;
import com.dukla.web.pojo.UserModuleListVo;
import com.dukla.web.pojo.UserModuleTreeVo;

import java.util.List;

/**
 * Created by dukla on 10/26/16.
 */
public interface UserHandler {
    /*
     * 得到用户功能树
     */
    public UserModuleTreeVo getUserModuleTree(SysLogin sysLogin);

    /*
     * 得到角色模块树
     */
    public ModuleTreeVo getRoleModuleTree(SysRole sysRole);

    /*
     * 得到用户功能列表
     */
    public UserModuleListVo getUserModuleList(SysLogin sysLogin);

    /*
     * 得到角色功能列表
     */
    public List<ModuleVo> getRoleModuleList(SysRole sysRole);



    /*
     * 根据模块调整权限
     */
    public void updatePermitByModuleId(String moduleId, String[] roleIds);


    /*
     * 根据角色调整权限
     */
    public void updatePermitByRoleId(String roleId, String[] moduleIds);

    /*
     * 增加权限
     */
    public String addPermit(String roleId, String moduleId);

    /*
     * 删除权限
     */
    public void removePermit(String id);

    /*
     * 删除权限
     */
    public void removePermit(String roleId, String moduleId);

    /*
     * 更新功能项权限(根据功能Id)
     */
    public void updateFuncPermitByFuncId(String funcId, String[] roleIds);

    /*
     * 更新功能项权限(根据角色Id)
     */
    public void updateFuncPermitByRoleId(String roleId, String[] funcIds);

    /*
     * 增加功能项权限
     */
    public String addFuncPermit(String roleId, String funcId);

    /*
     * 删除功能项权限
     */
    public void removeFuncPermit(String roleId, String funcId);

    /*
     * 得到最大角色排序号
     */
    public int getMaxSysRoleOrder();

    /*
     * 得到最小角色排序号
     */
    public int getMinSysRoleOrder();

    /*
     * 保存SysRole
     */
    public SysRole saveSysRole(SysRole sysRole);

    /*
     * 删除角色
     */
    public void removeSysRole(String id);

    /*
     * 调整角色顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysRoleOrder(SysRole sysRole, int direct);

    /*
     * 得到最大用户排序号
     */
    public int getMaxSysLoginOrder();

    /*
     * 得到最小用户排序号
     */
    public int getMinSysLoginOrder();

    /*
     * 保存用户
     */
    public void saveSysLogin(SysLogin sysLogin);

    /*
     * 删除用户
     */
    public void removeSysLogin(String id);

    /*
     * 调整用户顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysLoginOrder(SysLogin sysLogin, int direct);

    /*
     * 增加角色用户
     */
    public String addLoginRole(String roleId, String loginId);

    /*
     * 删除角色用户
     */
    public void removeLoginRole(String roleId, String loginId);

    /*
     * 批量修改角色用户(根据角色id)
     */
    public void updateLoginRoleByRoleId(String roleId, String[] loginIds);

    /*
     * 批量修改角色用户(根据用户id)
     */
    public void updateLoginRoleByLoginId(String loginId, String[] roleIds);


}
