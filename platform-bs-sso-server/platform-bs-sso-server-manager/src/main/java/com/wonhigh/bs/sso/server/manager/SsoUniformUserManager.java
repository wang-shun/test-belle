package com.wonhigh.bs.sso.server.manager;

import com.wonhigh.bs.sso.server.common.vo.SsoUniformUserDTO;
import com.yougou.logistics.base.common.exception.DaoException;
import com.yougou.logistics.base.common.exception.ManagerException;
import com.yougou.logistics.base.common.exception.ServiceException;
import com.yougou.logistics.base.manager.BaseCrudManager;

/**
 * 统一用户账号 管理（mysql）
 * @author user
 *
 */
public interface SsoUniformUserManager extends BaseCrudManager {

	/**
	 * 查询绑定过业务系统账号
	 * @param bizCode
	 * @param bizLoginName
	 * @return
	 */
	int findByBizUser(String bizCode, String bizLoginName) throws ServiceException;

	SsoUniformUserDTO findByLoginName(String loginName) throws DaoException;

	SsoUniformUserDTO findByMobile(String mobile) throws DaoException;

	/**
	 * 修改Mysql和Ldap数据
	 * 
	 * @param user
	 * @return
	 * @throws ManagerException
	 */
	int updateToMysqlAndLdap(SsoUniformUserDTO user) throws ManagerException;
}
