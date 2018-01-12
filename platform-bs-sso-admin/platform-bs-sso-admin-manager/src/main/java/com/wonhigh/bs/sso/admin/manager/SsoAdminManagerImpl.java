package com.wonhigh.bs.sso.admin.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.admin.service.SsoAdminService;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:33:34
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service
public class SsoAdminManagerImpl extends BaseCrudManagerImpl implements SsoAdminManager {
	
	@Resource
    private SsoAdminService ssoAdminService;

    @Override
    public BaseCrudService init() {
        return ssoAdminService;
    }	
	
}
