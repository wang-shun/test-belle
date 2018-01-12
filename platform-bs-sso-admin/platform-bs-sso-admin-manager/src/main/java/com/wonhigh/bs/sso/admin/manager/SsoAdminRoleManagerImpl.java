package com.wonhigh.bs.sso.admin.manager;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.admin.common.constants.Properties;
import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.wonhigh.bs.sso.admin.service.SsoAdminRoleService;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:33:29
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service
public class SsoAdminRoleManagerImpl extends BaseCrudManagerImpl implements SsoAdminRoleManager {
	@Resource
	private SsoAdminRoleService ssoAdminRoleService;
	
    @Override
    public BaseCrudService init() {
        return ssoAdminRoleService;
    }	

	@Override
	public List<SsoAdminRoleDTO> findAll() {
		return ssoAdminRoleService.findAll();
	}

}
