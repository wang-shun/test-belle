package com.wonhigh.bs.sso.admin.service;

import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
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
	
	/**
	 * 保存到删除表
	 * @param deletedUser
	 * @return
	 */
	public int saveAsDeleted(SsoUniformUserDTO deletedUser);

}
