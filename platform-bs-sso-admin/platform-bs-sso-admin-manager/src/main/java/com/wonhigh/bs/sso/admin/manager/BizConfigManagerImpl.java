package com.wonhigh.bs.sso.admin.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.admin.service.BizConfigService;
import com.yougou.logistics.base.manager.BaseCrudManagerImpl;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:33:41
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service
public class BizConfigManagerImpl extends BaseCrudManagerImpl implements BizConfigManager {
	
	@Resource
    private BizConfigService bizConfigService;

    @Override
    public BaseCrudService init() {
        return bizConfigService;
    }
}
