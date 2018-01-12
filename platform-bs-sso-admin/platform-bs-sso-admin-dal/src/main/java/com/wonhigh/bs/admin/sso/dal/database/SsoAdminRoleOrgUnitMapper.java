package com.wonhigh.bs.admin.sso.dal.database;

import com.yougou.logistics.base.dal.database.BaseCrudMapper;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:37:57
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public  interface SsoAdminRoleOrgUnitMapper extends BaseCrudMapper {
	
	/**
	 * 根据角色id删除
	 * @param roleId
	 * @return
	 */
	public int deleteByRoleId(Integer roleId);

}
