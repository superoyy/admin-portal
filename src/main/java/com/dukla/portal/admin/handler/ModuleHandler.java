package com.dukla.portal.admin.handler;

import com.dukla.base.domain.SysModule;
import com.dukla.base.domain.SysModuleFunction;

/**
 * Created by dukla on 10/26/16.
 */
public interface ModuleHandler {

    /*
     * 得到最大模块排序号(根据upId)
     */
    public int getMaxSysModuleOrder(String upId);

    /*
     * 得到最小模块排序号(根据upId)
     */
    public int getMinSysModuleOrder(String upId);

    /*
     * 保存SysModule
     */
    public SysModule saveSysModule(SysModule sysModule);

    /*
     * 删除模块
     */
    public void removeSysModule(String id);

    /*
     * 调整模块顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysModuleOrder(SysModule sysModule, int direct);

    /*
     * 得到最大功能项排序号(根据moduleId)
     */
    public int getMaxSysModuleFunctionOrder(String moduleId);

    /*
     * 得到最小功能项排序号(根据moduleId)
     */
    public int getMinSysModuleFunctionOrder(String moduleId);

    /*
     * 保存SysModuleFunction
     */
    public SysModuleFunction saveSysModuleFunction(SysModuleFunction sysModuleFunction);

    /*
     * 删除功能项
     */
    public void removeSysModuleFunction(String id);

    /*
     * 调整功能项顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysModuleFunctionOrder(SysModuleFunction sysModuleFunction, int direct);

}
