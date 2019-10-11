package com.dukla.web.pojo;

import com.dukla.base.domain.SysModule;
import com.dukla.base.domain.SysModuleFunction;

import java.io.Serializable;
import java.util.List;


/**
 * 功能树值对象
 * @version 1.0
 * @author 欧阳亚
 * @since 2011.12
 */
public class ModuleTreeVo implements Serializable{
	
	private SysModule sysModule;

	private List<SysModuleFunction> sysModuleFunctionList;
	
	private List<ModuleTreeVo> subModuleTreeVoList;
	
	private ModuleTreeVo parentModuleTreeVo;
	
	
	public SysModule getSysModule() {
		return sysModule;
	}

	public void setSysModule(SysModule sysModule) {
		this.sysModule = sysModule;
	}

	public List<SysModuleFunction> getSysModuleFunctionList() {
		return sysModuleFunctionList;
	}

	public void setSysModuleFunctionList(
			List<SysModuleFunction> sysModuleFunctionList) {
		this.sysModuleFunctionList = sysModuleFunctionList;
	}

	public List<ModuleTreeVo> getSubModuleTreeVoList() {
		return subModuleTreeVoList;
	}

	public void setSubModuleTreeVoList(List<ModuleTreeVo> subModuleTreeVoList) {
		this.subModuleTreeVoList = subModuleTreeVoList;
	}

	public ModuleTreeVo getParentModuleTreeVo() {
		return parentModuleTreeVo;
	}

	public void setParentModuleTreeVo(ModuleTreeVo parentModuleTreeVo) {
		this.parentModuleTreeVo = parentModuleTreeVo;
	}


}
