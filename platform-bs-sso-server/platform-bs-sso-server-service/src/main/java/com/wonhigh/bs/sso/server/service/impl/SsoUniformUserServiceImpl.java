package com.wonhigh.bs.sso.server.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wonhigh.bs.sso.dal.database.SsoUniformUserMapper;
import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.wonhigh.bs.sso.service.SsoUniformUserService;
import com.yougou.logistics.base.common.exception.DaoException;
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
	public SsoUniformUserDTO findbyCondition(Map<String, Object> map) throws DaoException {
			List<SsoUniformUserDTO> dtoList = ssoUniformUserMapper.findByCondition(map);
			if (dtoList.size() > 0) {
				return dtoList.get(0);
			}

		return null;
	}

}