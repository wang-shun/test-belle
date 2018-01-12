package com.wonhigh.bs.sso.admin.manager;

import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 请写出类的用途 
 * @author zhang.rq.com
 * @date  2017-03-22 17:12:35
 * @version 0.1.0
 * @copyright (C) 2013 YouGou Information Technology Co.,Ltd 
 * All Rights Reserved. 
 * 
 * The software for the YouGou technology development, without the 
 * company's written consent, and any other individuals and 
 * organizations shall not be used, Copying, Modify or distribute 
 * the software.
 * 
 */
public interface SsoAdminRoleOrgUnitManager extends BaseCrudManager {
	
	/**
	 * 更具角色id 删除
	 * @param roleId
	 * @return
	 * @throws ManagerException
	 */
	public int deleteByRoleId(Integer roleId) throws ManagerException;
	
}