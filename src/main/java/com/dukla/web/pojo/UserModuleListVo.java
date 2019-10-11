package com.dukla.portal.admin.web.pojo;

import com.timanetworks.iov.domain.SysLogin;
import com.timanetworks.iov.domain.SysRole;

import java.io.Serializable;
import java.util.List;

/**
 * 用户功能模块值对象
 * @version 1.0
 * @author 欧阳亚
 * @since 2011.12
 */
public class UserModuleListVo implements Serializable{
	
	private SysLogin sysLogin;
	
	private List<SysRole> sysRoleList;
	
	private List<ModuleVo> moduleVoList;

	public SysLogin getSysLogin() {
		return sysLogin;
	}

	public void setSysLogin(SysLogin sysLogin) {
		this.sysLogin = sysLogin;
	}

	public List<SysRole> getSysRoleList() {
		return sysRoleList;
	}

	public void setSysRoleList(List<SysRole> sysRoleList) {
		this.sysRoleList = sysRoleList;
	}

	public List<ModuleVo> getModuleVoList() {
		return moduleVoList;
	}

	public void setModuleVoList(List<ModuleVo> moduleVoList) {
		this.moduleVoList = moduleVoList;
	}

}
