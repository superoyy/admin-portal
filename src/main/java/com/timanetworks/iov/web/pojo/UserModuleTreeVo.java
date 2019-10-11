package com.timanetworks.iov.web.pojo;


import com.timanetworks.iov.domain.SysLogin;
import com.timanetworks.iov.domain.SysModuleFunction;
import com.timanetworks.iov.domain.SysRole;

import java.io.Serializable;
import java.util.List;

/**
 * 用户功能树值对象
 * @version 1.0
 * @author 欧阳亚
 * @since 2013.8
 */
public class UserModuleTreeVo implements Serializable{
	
	private SysLogin sysLogin;
	
	private List<SysRole> sysRoleList;
	
	private ModuleTreeVo moduleTreeVo;

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

	public ModuleTreeVo getModuleTreeVo() {
		return moduleTreeVo;
	}

	public void setModuleTreeVo(ModuleTreeVo moduleTreeVo) {
		this.moduleTreeVo = moduleTreeVo;
	}

    /**
     * 判断是否属于角色
     * @param roleId
     * @return
     */
    public boolean belongToRole(String roleId){
        boolean yes=false;
        if(this.sysRoleList!=null && roleId!=null){
            for(SysRole sysRole:this.sysRoleList){
               if(roleId.equals(sysRole.getId())){
                   yes=true;
                   break;
               }
            }
        }
        return yes;
    }

    /**
     * 根据urlKey判断是否有权限
     */
    public boolean hasPermit(String url){
        found=false;
        this.hasUrl(this.moduleTreeVo,url);
        return found;
    }

    Boolean found;


    private void hasUrl(ModuleTreeVo treeVo,String url){
        if(!found && treeVo!=null && url!=null && url.length()!=0){
            if(treeVo.getSysModule()!=null && url.equals(treeVo.getSysModule().getUrl())){
                found=true;
            }
            if(!found && treeVo.getSysModuleFunctionList() != null){
                for(SysModuleFunction moduleFunction:treeVo.getSysModuleFunctionList()){
                    if(url.equals(moduleFunction.getUrl())){
                        found=true;
                        break;
                    }
                }
            }
            if(!found){
                if(treeVo.getSubModuleTreeVoList() != null){
                    for(ModuleTreeVo moduleTreeVo:treeVo.getSubModuleTreeVoList()){
                        hasUrl(moduleTreeVo,url);
                    }
                }
            }
        }
    }

}
