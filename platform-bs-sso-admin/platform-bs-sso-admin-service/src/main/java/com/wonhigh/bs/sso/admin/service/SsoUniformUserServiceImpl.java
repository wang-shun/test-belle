package com.wonhigh.bs.sso.admin.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.admin.sso.dal.database.SsoAdminMapper;
import com.wonhigh.bs.admin.sso.dal.database.SsoUniformUserMapper;
import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;
import com.yougou.logistics.base.service.BaseCrudServiceImpl;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月18日 下午1:53:18
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
@Service("ssoUniformUserService")
class SsoUniformUserServiceImpl extends BaseCrudServiceImpl implements SsoUniformUserService {
    @Resource
    private SsoUniformUserMapper ssoUniformUserMapper;

    @Override
    public BaseCrudMapper init() {
        return ssoUniformUserMapper;
    }

	@Override
	public int saveAsDeleted(SsoUniformUserDTO deletedUser) {
		return ssoUniformUserMapper.saveAsDeleted(deletedUser);
	}

}