package com.wonhigh.bs.admin.sso.dal.database;

import com.wonhigh.bs.sso.admin.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:38:38
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public  interface SsoUniformUserMapper extends BaseCrudMapper {
	
	/**
	 * 保存到删除表
	 * @param deletedUser
	 * @return
	 */
	public int saveAsDeleted(SsoUniformUserDTO deletedUser);

}
