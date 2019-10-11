package com.dukla.portal.admin.handler;

import com.dukla.base.domain.SysDept;

/**
 * Created by dukla on 10/26/16.
 */
public interface DepartmentHandler {

    /*
     * 得到最大部门排序号(根据upId)
     */
    public int getMaxSysDeptOrder(String upId);

    /*
     * 得到最小部门排序号(根据upId)
     */
    public int getMinSysDeptOrder(String upId);

    /*
     * 保存SysDept
    */
    public SysDept saveSysDept(SysDept sysDept);

    /*
     * 删除部门
     */
    public void removeSysDept(String id);

    /*
     * 调整部门顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysDeptOrder(SysDept sysDept, int direct);

}
