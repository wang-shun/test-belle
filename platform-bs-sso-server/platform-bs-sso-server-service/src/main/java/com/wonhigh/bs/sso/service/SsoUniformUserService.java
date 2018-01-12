package com.wonhigh.bs.sso.service;

import java.util.Map;

import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.service.BaseCrudService;

/**
 * 
 * 统一账号用户管理（mysql）
 * 
 * @author user
 * @date 2017年11月18日 下午1:53:45
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public interface SsoUniformUserService extends BaseCrudService {

	public SsoUniformUserDTO findbyCondition(Map<String,Object> map) throws DaoException;
}
