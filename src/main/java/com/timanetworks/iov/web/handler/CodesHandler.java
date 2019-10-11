package com.timanetworks.iov.web.handler;

import com.timanetworks.iov.domain.SysCodes;

import java.util.List;

/**
 * Created by dukla on 10/26/16.
 */
public interface CodesHandler {
    /**
     * 得到系统代码(根据Type)
     * @param codeType
     * @return
     */
    public List<SysCodes> getSysCodesByType(String codeType);

    /*
     * 得到系统代码(根据Key)
     */
    public SysCodes getSysCodesByKey(String codeType, String codeKey);


    /*
     * 得到最大代码排序号(根据codeType,upId)
     */
    public int getMaxSysCodesOrder(String codeType, String upId);

    /*
     * 得到最小代码排序号(根据codeType,upId)
     */
    public int getMinSysCodesOrder(String codeType, String upId);

    /*
     * 保存sysCodes
     */
    public SysCodes saveSysCodes(SysCodes sysCodes);

    /*
     * 删除sysCodes
     */
    public void removeSysCodes(String id);

    /*
     * 调整代码顺序
     * direct 方向 0:顶 1:上 2:下 3:底
     */
    public void updateSysCodesOrder(SysCodes sysCodes, int direct);

}
