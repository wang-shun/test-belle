package com.wonhigh.bs.sso.admin.manager;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 系统管理员角色 管理
 * 
 * @author user
 * @date 2017年11月12日 下午3:27:10
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoAdminRoleManager extends BaseCrudManager{
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<SsoAdminRoleDTO> findAll() ;
}
