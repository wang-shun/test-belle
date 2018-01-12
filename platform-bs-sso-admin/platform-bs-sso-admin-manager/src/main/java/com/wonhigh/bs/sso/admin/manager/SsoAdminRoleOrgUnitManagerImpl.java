package com.wonhigh.bs.sso.admin.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.admin.service.SsoAdminRoleOrgUnitService;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 请写出类的用途 
 * @author zhang.rq.com
 * @date  2017-10-24 17:12:35
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
@Service("ssoAdminRoleOrgUnitManager")
public class SsoAdminRoleOrgUnitManagerImpl extends BaseCrudManagerImpl implements SsoAdminRoleOrgUnitManager {
    @Resource
    private SsoAdminRoleOrgUnitService ssoAdminRoleOrgUnitService;

    @Override
    public BaseCrudService init() {
        return ssoAdminRoleOrgUnitService;
    }

	@Override
	public int deleteByRoleId(Integer roleId) throws ManagerException {
		try{
			return ssoAdminRoleOrgUnitService.deleteByRoleId(roleId);
		}catch(ServiceException e)
        {
            throw new ManagerException((new StringBuilder()).append("Problem invoking method, Cause:").append(e.getMessage()).toString(), e);
        }
	}

}