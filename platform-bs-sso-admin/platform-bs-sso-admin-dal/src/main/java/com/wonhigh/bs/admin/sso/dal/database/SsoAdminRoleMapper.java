package com.wonhigh.bs.admin.sso.dal.database;

import java.util.List;

import com.wonhigh.bs.sso.admin.common.vo.SsoAdminRoleDTO;
import com.yougou.logistics.base.dal.database.BaseCrudMapper;

/**
 * 
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午3:38:20
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public  interface SsoAdminRoleMapper extends BaseCrudMapper {

	/**
	 * 查询所有角色
	 * @return
	 */
	public List<SsoAdminRoleDTO> findAll();

}
